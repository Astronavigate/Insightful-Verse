package tech.ravon.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.ravon.model.inver.Annotation;
import tech.ravon.service.inver.AnnotationService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InsightfulVerseRestController {

    private final AnnotationService annotationService;

    // JSON helper
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    /** FastAPI 请求 DTO */
    private record AiRequest(String query, String table_name) {}

    /**
     * 本地中断标志：sessionId -> AtomicBoolean
     * 当 front-end 请求中断时，我们会：
     *  1) 在本地 set true（让流读取线程尽快退出）
     *  2) 同时调用 Python 的 /ask/interrupt/{sessionId}，让 Python 停止生成（真正释放模型资源）
     */
    private static final Map<String, AtomicBoolean> INTERRUPT_FLAGS = new ConcurrentHashMap<>();

    /* ----------------------- Annotation APIs（原样保留） ----------------------- */

    @GetMapping("/annotation/list")
    public ResponseEntity<?> list(@RequestParam("bookId") Long bookId, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        tech.ravon.model.inver.User user = (tech.ravon.model.inver.User) userObj;
        List<Annotation> list = annotationService.getAnnotations(user.getUserId(), bookId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/annotation/save")
    public ResponseEntity<?> save(@RequestBody Annotation annotation, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not_logged_in"));
        }
        tech.ravon.model.inver.User user = (tech.ravon.model.inver.User) userObj;
        annotation.setUserId(user.getUserId());
        try {
            annotationService.saveAnnotation(annotation);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "save_failed", "message", e.getMessage()));
        }
    }

    @PostMapping("/annotation/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not_logged_in"));
        }
        tech.ravon.model.inver.User user = (tech.ravon.model.inver.User) userObj;
        try {
            annotationService.deleteAnnotation(id, user.getUserId());
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "delete_failed"));
        }
    }

    /* ----------------------- ✅ AI Streaming（支持完全打断） ----------------------- */

    @PostMapping(
            value = "/AiBot/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamAiResponse(@RequestBody AiRequest aiBotRequest) {

        String fullContent = aiBotRequest.query();
        System.out.println(aiBotRequest.toString());
        if (fullContent == null || fullContent.isBlank()) {
            log.warn("Null sequence detected: query is empty.");
        } else {
            log.info("Logic Stream Initialized -> Payload Length: {}", fullContent.length());
        }

        SseEmitter emitter = new SseEmitter(0L);

        Thread.ofVirtual().start(() -> {
            String pythonUrl = "http://127.0.0.1:7824/ask/stream";
            try {
                // 构建要发送给 Python 的 JSON body
                Map<String, Object> payload = new HashMap<>();
                payload.put("query", fullContent);
                payload.put("table_name", "");
                final String jsonBody = GSON.toJson(payload);
                final byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

                RestTemplate restTemplate = new RestTemplate();

                RequestCallback requestCallback = request -> {
                    request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    request.getHeaders().setContentLength(bodyBytes.length);
                    try (OutputStream os = request.getBody()) {
                        os.write(bodyBytes);
                        os.flush();
                    }
                };

                ResponseExtractor<Void> responseExtractor = (ClientHttpResponse response) -> {
                    // 1) 如果 Python 返回错误状态，直接报错并结束
                    if (response.getStatusCode().isError()) {
                        String err = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        log.error("Upstream Disruption: {}", err);
                        emitter.completeWithError(new RuntimeException("FastAPI_Error: " + err));
                        return null;
                    }

                    // 2) 读取 Python 返回的 header X-Session-ID（Python 会在响应头里放 session id）
                    String pythonSessionId = response.getHeaders().getFirst("X-Session-ID");
                    if (pythonSessionId == null || pythonSessionId.isBlank()) {
                        // 如果 Python 没返回，则生成一个本地 id（仍然可用于本地中断），但中断真正作用仍需要与 Python 协同
                        pythonSessionId = UUID.randomUUID().toString();
                        log.warn("Python did not return X-Session-ID; generated local sessionId={}", pythonSessionId);
                    }

                    // 在本地注册中断标志
                    AtomicBoolean stopFlag = new AtomicBoolean(false);
                    INTERRUPT_FLAGS.put(pythonSessionId, stopFlag);

                    // 3) 将 sessionId 先发送给前端，方便前端/Java 后续调用中断接口
                    try {
                        String sessionJson = GSON.toJson(Map.of("session_id", pythonSessionId));
                        emitter.send(SseEmitter.event().data(sessionJson));
                    } catch (Exception e) {
                        log.warn("Failed to send session id to client: {}", e.getMessage());
                    }

                    // 4) 逐行读取 Python 的 SSE（注意：Python 发出的每条消息已经是 "data: ..." 或直接 JSON）
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.isBlank()) continue;

                            // 在读取循环中检查本地 stopFlag（当前端要求中断时，Java 会把本地 flag 设为 true）
                            if (stopFlag.get()) {
                                // 告知前端我们已中断并结束流
                                try {
                                    emitter.send(SseEmitter.event().data("{\"type\":\"error\",\"message\":\"Interrupted by user (java)\"}"));
                                } catch (Exception ignored) {}
                                break;
                            }

                            String data = line.startsWith("data:") ? line.substring(5).trim() : line.trim();
                            if (!data.isEmpty()) {
                                // 直接把 Python 的 data payload 转发给前端
                                emitter.send(SseEmitter.event().data(data));
                            }
                        }

                        // 结束标记
                        try {
                            emitter.send(SseEmitter.event().data("[STOP]"));
                        } catch (Exception ignored) {}
                        emitter.complete();
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    } finally {
                        // 清理本地中断标志
                        INTERRUPT_FLAGS.remove(pythonSessionId);
                    }
                    return null;
                };

                // 发起请求并流式处理响应
                restTemplate.execute(pythonUrl, HttpMethod.POST, requestCallback, responseExtractor);

            } catch (Exception e) {
                log.error("Core Engine Failure", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * 前端或其它服务调用此接口来请求打断（Java 将：
     *  1) 在本地设置中断标志（让当前转发线程尽快退出）
     *  2) 主动调用 Python 的 interrupt 接口 /ask/interrupt/{sessionId}（真正请求 Python 停止 LLM 生成）
     */
    @PostMapping("/AiBot/interrupt/{sessionId}")
    public ResponseEntity<?> interruptStream(@PathVariable String sessionId) {
        AtomicBoolean flag = INTERRUPT_FLAGS.get(sessionId);
        // 先在本地标记
        if (flag != null) {
            flag.set(true);
        } else {
            // 没找到本地线程/会话，也允许仍然去调用 Python 中断（以防 Python side 有会话）
            log.warn("interruptStream: local flag not found for session {}, will still call Python interrupt", sessionId);
        }

        // 调用 Python 的中断接口（在 Python 端会设置 stop_event）
        String pythonInterruptUrl = "http://127.0.0.1:7824/ask/interrupt/" + sessionId;
        RestTemplate rest = new RestTemplate();
        try {
            // POST without body
            rest.postForObject(pythonInterruptUrl, null, String.class);
            return ResponseEntity.ok(Map.of("status", "interrupt_sent", "sessionId", sessionId));
        } catch (Exception e) {
            log.error("Failed to call Python interrupt for session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "python_interrupt_failed", "message", e.getMessage()));
        }
    }
}