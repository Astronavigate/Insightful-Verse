/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ravon.service.iviep.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.ravon.service.iviep.AiBotService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AiBotServiceImpl implements AiBotService {

    @Value("${llama.api.base_url:http://localhost:2268}")
    private String LLAMA_API_BASE_URL;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, Thread> activeStreamThreads = new ConcurrentHashMap<>();

    public AiBotServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)  // 使用 HTTP/1.1 避免不支持的升级请求
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void streamGenerateText(String sessionId, String prompt, int maxTokens, TextStreamCallback callback) {
        // 中断旧的流
        if (activeStreamThreads.containsKey(sessionId)) {
            interruptClientStream(sessionId);
        }

        Thread streamThread = new Thread(() -> {
            HttpResponse<InputStream> response = null;
            try {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("prompt", prompt);
                requestData.put("max_tokens", maxTokens);
                requestData.put("stream", true);
                String requestBody = objectMapper.writeValueAsString(requestData);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(LLAMA_API_BASE_URL + "/stream_generate"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "text/event-stream")  // 明确接收 SSE
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
                if (response.statusCode() != 200) {
                    String body = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                    throw new IOException("Stream API failed with status " + response.statusCode() + ": " + body);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (Thread.currentThread().isInterrupted()) break;
                        if (line.startsWith("data:")) {
                            String data = line.substring(5).trim();
                            if ("[STOP]".equals(data)) break;
                            if (data.startsWith("[ERROR:")) {
                                callback.onError(new IOException(data));
                                return;
                            }
                            callback.onNewText(data);
                        } else if (line.startsWith("event: end")) {
                            break;
                        }
                    }
                    callback.onComplete();
                }
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                activeStreamThreads.remove(sessionId);
            }
        }, "llama-stream-" + sessionId);

        activeStreamThreads.put(sessionId, streamThread);
        streamThread.start();
    }

    @Override
    public boolean interruptClientStream(String sessionId) {
        Thread t = activeStreamThreads.get(sessionId);
        if (t != null && t.isAlive()) {
            t.interrupt();
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(LLAMA_API_BASE_URL + "/interrupt_stream"))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                httpClient.send(req, HttpResponse.BodyHandlers.discarding());
            } catch (Exception ignored) {}
            return true;
        }
        return false;
    }

    @Override
    public String interruptLlamaStream() {
        return null;
    }
}
