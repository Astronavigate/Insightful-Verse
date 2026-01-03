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

package tech.ravon.controller;

import jakarta.servlet.http.*;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.ravon.model.inver.Course;
import tech.ravon.model.inver.File;
import tech.ravon.model.inver.User;
import tech.ravon.model.inver.ViewRecord;
import tech.ravon.service.inver.*;
import tech.ravon.vo.inver.CourseVO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Controller
public class InsightfulVerseController {

    @Autowired
    UserService userService;
    @Autowired
    CourseService courseService;
    @Autowired
    FileService fileService;
    @Autowired
    ViewRecordService viewRecordService;
    @Autowired
    VersionService iVVersionService;
    @Autowired
    AiBotService aiBotService;
    @Autowired
    MailService mailService;
    @Autowired
    CodeService codeService;
    @Autowired
    PageService pageService;
    @Autowired
    FavoriteService favoriteService;
    @Autowired
    HaloService haloService;

    @RequestMapping("/InsightfulVerse/")
    public String IVMain() {
        return "InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/index")
    public String IVIndex() {
        return "InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/About")
    public String IVAbout() {
        return "InsightfulVerse/About";
    }

    @RequestMapping("/InsightfulVerse/Code")
    public String IVCode(HttpServletRequest request) {
        File file = null;
        if (request.getSession().getAttribute("codeFile") != null) {
            file = (File) request.getSession().getAttribute("file");
            request.getSession().removeAttribute("codeFile");
        }
        if (file == null || file.getFilePath() == null || file.getFilePath().isEmpty()) {
            request.setAttribute("fileId", null);
            request.setAttribute("lang", null);
            request.setAttribute("codeContent", null);
        } else {
            String classpath = System.getProperty("user.dir") + "/src/main/resources/static";
            String filePath = classpath + file.getFilePath();
            String fileType = switch (file.getType().toLowerCase()) {
                case "c" -> "c";
                case "c++", "cpp" -> "cpp";
                case "java" -> "java";
                case "py" -> "python";
                case "rs" -> "rust";
                default -> file.getType();
            };

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            request.setAttribute("fileId", file.getFileId());
            request.setAttribute("lang", fileType);
            request.setAttribute("codeContent", sb.toString());
        }
        return "InsightfulVerse/Code";
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/Code.run")
    public String IVXCode(HttpServletRequest request, HttpServletResponse response) {
        String result;
        try {
            result = codeService.runCode(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", e.getMessage());
            return "redirect:/InsightfulVerse/Error";
        }
        return result;
    }

    @RequestMapping("/InsightfulVerse/Course")
    public String IVCourse(HttpServletRequest request) {
        List<CourseVO> courseList = courseService.allCourseVO(request);
        System.out.println(courseList.toString());
        request.setAttribute("courseList", courseList);
        return "InsightfulVerse/Course";
    }

    @RequestMapping("/InsightfulVerse/CourseInfo")
    public String IVCourseInfo(HttpServletRequest request) {
        Long courseId = Long.valueOf(request.getParameter("courseId"));
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            if (user.getAuthority() == "infinite" && (boolean) request.getSession().getAttribute("authorize")) {
                request.getSession().setAttribute("lastUrl", request.getRequestURL());
                return "redirect:/InsightfulVerse/VerifyPerm";
            }
        }
        List<File> sourceList = fileService.getCourseFiles(courseId);
        request.setAttribute("sourceList", sourceList);
        return "InsightfulVerse/CourseInfo";
    }

    @RequestMapping("/InsightfulVerse/Error")
    public String error(HttpServletRequest request) {
        request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
        request.getSession().setAttribute("errorMessage", null);
        return "InsightfulVerse/Error";
    }

    @RequestMapping("/InsightfulVerse/Login")
    public String IVLogin() {
        return "InsightfulVerse/Login";
    }

    @RequestMapping("/InsightfulVerse/Login.do")
    public String IVXLogin(HttpServletRequest request, HttpServletResponse response) {
        User user = userService.Login(request, response);
        if (user == null || user.getUserId() == null) {
            request.getSession().setAttribute("errorMessage", "Login failed, please check your username and password and try again.");
            return "redirect:/InsightfulVerse/Error";
        } else {
            request.getSession().setAttribute("user", user);
            return "redirect:/InsightfulVerse/index";
        }
    }

    @RequestMapping("/InsightfulVerse/Logout")
    public String IVLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        userService.Logout(request, response);
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/Personal")
    public String IVersonal(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/InsightfulVerse/Login";
        }
        List<ViewRecord> viewRecordList = viewRecordService.recentViewedFile(user.getUserId());
        request.setAttribute("viewRecordList", viewRecordList);
        request.setAttribute("ivVersion", iVVersionService.getLatestVersion());
        return "InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/Player")
    public String IVPlayer(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        request.setAttribute("file", file);
        switch (file.getType().toLowerCase()) {
            case "mp4", "mkv", "mov", "wmv" -> {
                return "InsightfulVerse/VideoPlayer";
            }
            case "wav", "wma", "mp3", "flac", "m4a" -> {
                String filepath = System.getProperty("user.dir") + "/src/main/resources/static" + file.getFilePath();
                java.io.File iofile = new java.io.File(filepath);

                if (iofile != null && iofile.exists()) {
                    System.out.println("file exists");
                    try {
                        AudioFile audioFile = AudioFileIO.read(iofile);
                        Tag tag = audioFile.getTag();
                        AudioHeader header = audioFile.getAudioHeader();

                        // 输出所有标签信息
                        if (tag != null) {
                            for (FieldKey key : FieldKey.values()) {
                                try {
                                    String value = tag.getFirst(key);
                                    if (value != null && !value.trim().isEmpty()) {
                                        request.setAttribute(key.name().toLowerCase(), value);
                                        System.out.println(key.name() + ": " + value);
                                    }
                                } catch (UnsupportedOperationException | KeyNotFoundException e) {
                                    // 忽略不支持或不存在的字段
                                }
                            }

                            // 获取封面
                            List<Artwork> artworkList = tag.getArtworkList();
                            if (!artworkList.isEmpty()) {
                                Artwork artwork = artworkList.get(0);
                                byte[] imageData = artwork.getBinaryData();
                                String mime = artwork.getMimeType();
                                String base64 = Base64.getEncoder().encodeToString(imageData);
                                String dataUri = "data:" + mime + ";base64," + base64;
                                request.setAttribute("cover", dataUri);

                                // 输出 Base64 前 20 个字符
                                System.out.println("Cover(Base64 20 chars): " + (base64.length() > 20 ? base64.substring(0, 20) : base64));
                            }
                        }

                        // 音频文件信息
                        if (header != null) {
                            request.setAttribute("duration", header.getTrackLength());
                            request.setAttribute("bitrate", header.getBitRate());
                            request.setAttribute("sampleRate", header.getSampleRateAsNumber());
                            request.setAttribute("channels", header.getChannels());
                            request.setAttribute("encodingType", header.getEncodingType());

                            System.out.println("Duration: " + header.getTrackLength());
                            System.out.println("Bitrate: " + header.getBitRate());
                            System.out.println("SampleRate: " + header.getSampleRateAsNumber());
                            System.out.println("Channels: " + header.getChannels());
                            System.out.println("EncodingType: " + header.getEncodingType());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return "InsightfulVerse/AudioPlayer";
            }
        }
        System.out.println(file);
        return "InsightfulVerse/Player";
    }

    @RequestMapping("/InsightfulVerse/Reader")
    public String IVReader(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        request.setAttribute("file", file);
        switch (file.getType().toLowerCase()) {
            case "doc", "docx" -> {
                return "InsightfulVerse/Reader/WordReader";
            }
            case "xls", "xlsm", "xlsx" -> {
                return "InsightfulVerse/Reader/ExcelReader";
            }
            case "ppt", "pptx" -> {
                return "InsightfulVerse/Reader/PowerPointReader";
            }
            case "pdf" -> {
                return "InsightfulVerse/Reader/PostScriptReader";
            }
            case "epub" -> {
                return "InsightfulVerse/Reader/BookReader";
            }
            default -> {
                return "InsightfulVerse/Reader/TextReader";
            }
        }
    }

    @RequestMapping("/InsightfulVerse/Painter")
    public String IVPainter(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        request.setAttribute("file", file);
        return "/InsightfulVerse/Painter";
    }

    @RequestMapping("/InsightfulVerse/Register")
    public String IVRegister() {
        return "InsightfulVerse/Register";
    }

    @RequestMapping("/InsightfulVerse/Register.do")
    public String IVXRegister(HttpServletRequest request, HttpServletResponse response) {
        String message = userService.Register(request, response);
        if (message == null) {
            return "redirect:/InsightfulVerse/index";
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
    }

    @RequestMapping("/InsightfulVerse/Unregister")
    public String IVUnregister() {
        return "InsightfulVerse/Unregister";
    }

    @RequestMapping("/InsightfulVerse/Unregister.do")
    public String IVXUnregister(HttpServletRequest request, HttpServletResponse response) {
        String message = userService.Unregister(request, response);
        if (message != null) {
            request.getSession().setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
        request.getSession().removeAttribute("user");
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/UpdateUser")
    public String IVUpdateUser(HttpServletRequest request) {
        User user = userService.Userinfo(request);
        if (user == null) {
            request.getSession().setAttribute("errorMessage", "Please log in first.");
            return "redirect:/InsightfulVerse/Error";
        }
        request.setAttribute("user", user);
        return "InsightfulVerse/UpdateInfo";
    }

    @RequestMapping("/InsightfulVerse/UpdateUser.do")
    public String IVXUpdateUser(HttpServletRequest request, HttpServletResponse response) {
        String message = userService.UpdateUser(request, response);
        System.out.println(message == null ? "null-message" : message);
        if (message != null) {
            request.getSession().setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
        User user = userService.Userinfo(request);
        request.getSession().setAttribute("user", user);
        return "redirect:/InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/NihilityZone")
    public String IVNihilityZone(HttpServletRequest request) {
        String codeContent = (String) request.getSession().getAttribute("codeContent");
        List<List<String>> resultList = (List<List<String>>) request.getSession().getAttribute("resultList");
        request.getSession().removeAttribute("resultList");
        request.getSession().removeAttribute("codeContent");
        request.setAttribute("resultList", resultList);
        request.setAttribute("codeContent", codeContent);
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null) {
            return "redirect:/InsightfulVerse/";
        } else {
            if (user.getAuthority().equals("infinite") && (request.getSession().getAttribute("authorize") == null || !(boolean) request.getSession().getAttribute("authorize"))) {
                return "redirect:/InsightfulVerse/VerifyPerm";
            } else if (!user.getAuthority().equals("infinite")) {
                return "redirect:/InsightfulVerse/Personal";
            }
        }
        return "InsightfulVerse/NihilityZone";
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/VerifyCode")
    public String IVVerifyCode(HttpServletRequest request) {
        return mailService.getCaptcha(request);
    }

    @RequestMapping("/InsightfulVerse/File")
    public String IVFile(HttpServletRequest request, HttpServletResponse response) {
        Long fileId = Long.valueOf(request.getParameter("fileId"));
        File file = fileService.getFileById(fileId);
        request.getSession().setAttribute("file", file);
        switch (file.getType().toLowerCase()) {
            case "pdf", "ppt", "pptx", "xls", "xlsm", "xlsx", "doc", "docx", "epub" -> {
                return "redirect:/InsightfulVerse/Reader";
            }
            case "mp4", "mkv", "mov", "wmv", "wav", "wma", "mp3", "flac", "m4a" -> {
                return "redirect:/InsightfulVerse/Player";
            }
            case "jpg", "jpeg", "heif", "raw", "png", "gif", "webp", "ico" -> {
                return "redirect:/InsightfulVerse/Painter";
            }
            case "cpp", "py", "c", "h", "java", "html", "css", "js", "jsp", "php", "aspx", "ts", "rs", "sql" -> {
                request.getSession().setAttribute("codeFile", true);
                return "redirect:/InsightfulVerse/Code";
            }
            default -> {
                return "redirect:/InsightfulVerse/Reader";
            }
        }
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/Timer")
    public String IVTimer(HttpServletRequest request) {
        System.out.println("User viewing file " + request.getParameter("fileId"));
        viewRecordService.saveViewRecord(request);
        return null;
    }

    @RequestMapping("/InsightfulVerse/UpdFile")
    public String IVUpdFile(HttpServletRequest request, HttpServletResponse response) {
        String courseId = request.getParameter("courseId");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null || (!user.getAuthority().equals("admin") && !user.getAuthority().equals("infinite"))) {
            return "redirect:" + request.getHeader("Referer");
        }
        fileService.updFile(request, response);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping("/InsightfulVerse/DelFile")
    public String IVDelFile(HttpServletRequest request) {
        Long fileId = Long.valueOf(request.getParameter("fileId"));
        String courseId = request.getParameter("courseId");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null || (!user.getAuthority().equals("infinite") && !user.getAuthority().equals("admin"))) {
            return "redirect:/InsightfulVerse/CourseInfo?courseId=" + courseId;
        }
        fileService.deleteFile(request, fileId);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping("/InsightfulVerse/UpdCourse")
    public String IVUpdCourse(@RequestParam(required = false) String courseId,
                                 @RequestParam(required = false) String courseName,
                                 @RequestParam(required = false) String courseInfo,
                                 HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || (!user.getAuthority().equals("admin") && !user.getAuthority().equals("infinite"))) {
            return "redirect:" + request.getHeader("Referer");
        }
        if (courseName == null || courseName.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Please check your input.");
            return "redirect:/InsightfulVerse/Error";
        }
        Course course = new Course();
        if (courseId != null && !courseId.isEmpty()) {
            course.setCourseId(Long.valueOf(courseId));
        }
        course.setCourseName(courseName);
        course.setCourseInfo(courseInfo);
        courseService.updateCourse(course);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping("/InsightfulVerse/DelCourse")
    public String IVDelCourse(@RequestParam Long courseId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        // 权限验证
        if (user != null && user.getUserId() != null) {
            if ("infinite".equals(user.getAuthority())
                    && Boolean.TRUE.equals(request.getSession().getAttribute("authorize"))) {
                request.getSession().setAttribute("lastUrl", "/InsightfulVerse/CourseInfo?courseId=" + courseId);
                return "redirect:/InsightfulVerse/VerifyPerm";
            }
        }

        // 删除课程
        courseService.deleteCourse(request, courseId);

        // 获取来源页
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/InsightfulVerse/Search")) {
            return "redirect:" + referer; // 返回搜索页面
        }

        // 默认返回课程列表页
        return "redirect:/InsightfulVerse/Course";
    }

    @RequestMapping("/InsightfulVerse/VerifyPerm")
    public String IVVerifyPerm(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !user.getAuthority().equals("infinite")) {
            return "redirect:/InsightfulVerse/";
        }
        return "/InsightfulVerse/VerifyPermissions";
    }

    @RequestMapping("/InsightfulVerse/VerifyPerm.do")
    public String IVXVerifyPerm(HttpServletRequest request, HttpServletResponse response) {
        User user = userService.verifyPerm(request, response);
        if (user != null && user.getAuthority().equals("infinite")) {
            request.getSession().setAttribute("authorize", true);
        } else {
            return "redirect:/InsightfulVerse/index";
        }
        return "redirect:/InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/Halo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> IVHalo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");

        if (user != null && user.getUserId() != null) {
            if (user.getAuthority().equals("infinite")) {
                // 检查是否已授权
                if (request.getSession().getAttribute("authorize") == null ||
                        !(boolean) request.getSession().getAttribute("authorize")) {
                    response.put("status", "unauthorized");
                    response.put("redirectUrl", "/InsightfulVerse/VerifyPerm");
                    return ResponseEntity.status(401).body(response);
                } else {
                    // 已授权，执行SQL
                    String code = request.getParameter("code");
                    if (code == null || code.trim().isEmpty()) {
                        response.put("status", "error");
                        response.put("message", "SQL code cannot be empty");
                        return ResponseEntity.badRequest().body(response);
                    }

                    try {
                        List<List<String>> resultList = haloService.runSQL(code);
                        response.put("status", "success");
                        response.put("data", resultList);
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        response.put("status", "error");
                        response.put("message", "SQL execution error: " + e.getMessage());
                        return ResponseEntity.status(500).body(response);
                    }
                }
            } else {
                // 权限不足
                response.put("status", "forbidden");
                response.put("message", "Insufficient permissions");
                response.put("redirectUrl", "/InsightfulVerse/Personal");
                return ResponseEntity.status(403).body(response);
            }
        } else {
            // 未登录
            response.put("status", "unauthenticated");
            response.put("message", "User not logged in");
            response.put("redirectUrl", "/InsightfulVerse/Personal");
            return ResponseEntity.status(401).body(response);
        }
    }

    @RequestMapping("/InsightfulVerse/ViewHistory")
    public String IVViewHistory(HttpServletRequest request) {
        int max = request.getParameter("max") == null || request.getParameter("max").isEmpty() ? 15 : Integer.parseInt(request.getParameter("max"));
        int page = request.getParameter("page") == null || request.getParameter("page").isEmpty() ? 1 : Integer.parseInt(request.getParameter("page"));
        User user = (User) request.getSession().getAttribute("user");
        Long userId = null;
        if (user != null) {
            userId = user.getUserId();
        }
        if (userId == null) {
            request.getSession().setAttribute("errorMessage", "Please check your login statement and try again.");
            return "redirect:/InsightfulVerse/Error";
        }
        List<ViewRecord> viewRecordList = viewRecordService.viewedFile(userId);
        List<ViewRecord> pagedList = pageService.pageList(viewRecordList, page, max);
        int totalPages = (viewRecordList.size() + max - 1) / max;
        request.setAttribute("viewRecordList", pagedList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", viewRecordList.size());
        request.setAttribute("currentPage", page);
        return "InsightfulVerse/ViewHistory";
    }

    @RequestMapping("/InsightfulVerse/Search")
    public String Search(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            if (user.getAuthority() == "infinite" && (boolean) request.getSession().getAttribute("authorize")) {
                request.getSession().setAttribute("lastUrl", request.getRequestURL());
                return "redirect:/InsightfulVerse/VerifyPerm";
            }
        }
        String type = request.getParameter("type");
        String keyword = request.getParameter("keyword");
        if (!Objects.equals(type, "all") && !Objects.equals(type, "course") && !Objects.equals(type, "file")) {
            type = "all";
        }
        if (type.equals("course") || type.equals("all")) {
            List<Course> courseList = courseService.getCourseByName(keyword);
            System.out.println(courseList.size());
            request.setAttribute("courseList", courseList);
        }
        if (type.equals("file") || type.equals("all")) {
            List<File> fileList = fileService.getFileByName(keyword);
            System.out.println(fileList.size());
            request.setAttribute("fileList", fileList);
        }
        request.setAttribute("type", type);
        request.setAttribute("keyword", keyword);
        return "InsightfulVerse/Search";
    }

    @RequestMapping("/InsightfulVerse/Favorite.do")
    public String handleFavorite(
            @RequestParam Long id,
            @RequestParam String type,
            @RequestParam(required = false) String keyword,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            favoriteService.toggleFavorite(user.getUserId(), id, type);
        }

        if ("file".equalsIgnoreCase(type)) {
            return "redirect:/InsightfulVerse/CourseInfo?courseId=10000000000#sc" + id;
        } else {
            String redirectPath = "redirect:/InsightfulVerse/Course";
            if (keyword != null && !keyword.isEmpty()) {
                redirectPath += "?keyword=" + keyword;
            }
            return redirectPath + "#cs" + id;
        }
    }

    @RequestMapping("/InsightfulVerse/AiBot")
    public String AiBot(HttpServletRequest request) {
        return "InsightfulVerse/AiBot";
    }

    /**
     * 处理流式 AI 文本生成请求。
     * 使用 SseEmitter 实现服务器发送事件 (Server-Sent Events)
     * 将 AI 模型的流式输出实时推送到客户端。
     *
     * @param prompt 用户输入的提示词
     * @param session HttpSession 对象，用于获取会话ID作为流的唯一标识符
     * @return SseEmitter 对象，用于将数据流式传输到客户端
     */
    @RequestMapping("/InsightfulVerse/AiBot/stream")
    public SseEmitter streamAiResponse(@RequestParam String prompt, HttpSession session) {
        String sessionId = session.getId();
        System.out.println("New stream request for session: " + sessionId + " with prompt: " + prompt);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 单位：毫秒，Long.MAX_VALUE 表示几乎永不超时

        // 设置完成、超时和错误回调，以正确管理 SseEmitter 的生命周期
        emitter.onCompletion(() -> {
            System.out.println("SSE stream completed for session: " + sessionId);
            // 这里可以添加清理逻辑，例如从 Service 的 activeStreamThreads 中移除
            // 但因为 Service 层的 finally 块中已经处理了移除，这里可以省略或用于额外清理
        });

        emitter.onTimeout(() -> {
            System.out.println("SSE stream timed out for session: " + sessionId);
            emitter.complete(); // 超时时关闭连接
            // 尝试中断后端流
            aiBotService.interruptClientStream(sessionId);
        });

        emitter.onError(error -> {
            System.err.println("SSE stream error for session: " + sessionId + ": " + error.getMessage());
            emitter.completeWithError(error); // 错误时关闭连接并传递错误
            // 尝试中断后端流
            aiBotService.interruptClientStream(sessionId);
        });

        // 调用 Service 层的方法进行流式生成
        // 传入 sessionId 以便 Service 层跟踪和管理线程
        aiBotService.streamGenerateText(sessionId, prompt, 2048, new AiBotService.TextStreamCallback() {
            @Override
            public void onNewText(String textChunk) {
                try {
                    emitter.send(SseEmitter.event().data(textChunk));
                } catch (IOException e) {
                    System.err.println("Failed to send SSE event for session " + sessionId + ": " + e.getMessage());
                    emitter.completeWithError(e); // 发送失败时完成 SseEmitter
                }
            }

            @Override
            public void onComplete() {
                emitter.complete(); // 流正常完成
                System.out.println("AI Stream to client completed for session: " + sessionId);
            }

            @Override
            public void onError(Throwable error) {
                emitter.completeWithError(error); // 错误时完成 SseEmitter
                System.err.println("AI Stream error for session " + sessionId + ": " + error.getMessage());
            }
        });

        return emitter;
    }

    /**
     * 处理中断 AI 文本生成请求。
     * 前端通过 POST 请求调用此接口来中断正在进行的流。
     *
     * @param session HttpSession 对象，用于获取会话ID，以中断对应的流
     * @return 成功或失败消息的 JSON 字符串
     */
    @RequestMapping("/InsightfulVerse/AiBot/interrupt") // 例如：POST /InsightfulVerse/AiBot/interrupt
    public String interruptAiGeneration(HttpSession session) {
        String sessionId = session.getId();
        System.out.println("Interrupt request received for session: " + sessionId);
        // 尝试中断客户端线程，并会同时调用 Llama.cpp 后端的中断API
        boolean interrupted = aiBotService.interruptClientStream(sessionId);
        if (interrupted) {
            return "{\"message\": \"Interruption signal sent for session " + sessionId + ".\"}";
        } else {
            return "{\"message\": \"No active stream to interrupt for session " + sessionId + ".\"}";
        }
    }
}
