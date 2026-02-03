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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.ravon.lib.AudioTools;
import tech.ravon.lib.AudioTools.*;
import tech.ravon.model.GeoAccess;
import tech.ravon.model.inver.*;
import tech.ravon.service.GeoAccessService;
import tech.ravon.service.inver.*;
import tech.ravon.vo.inver.AudioInfoVO;
import tech.ravon.vo.inver.CourseVO;
import tech.ravon.vo.inver.FileVO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class InsightfulVerseController {

    private final UserService userService;
    private final CourseService courseService;
    private final FileService fileService;
    private final ViewRecordService viewRecordService;
    private final VersionService iVVersionService;
    private final AiBotService aiBotService;
    private final MailService mailService;
    private final CodeService codeService;
    private final PageService pageService;
    private final CollectionService collectionService;
    private final HaloService haloService;
    private final GeoAccessService geoAccessService;

    @RequestMapping("/InsightfulVerse/")
    public String ivReIndex() {
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/index")
    public String ivIndex(HttpServletRequest request) {
        log.info("Handle request: GET /InsightfulVerse/index");
        List<File> fileList = fileService.getFilesByPop(4L);
        request.setAttribute("fileList", fileList);
        return "InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/About")
    public String ivAbout() {
        log.info("Handle request: GET /InsightfulVerse/About");
        return "InsightfulVerse/About";
    }

    @RequestMapping("/InsightfulVerse/Code")
    public String ivCode(HttpServletRequest request) {
        log.info("Handle request: GET /InsightfulVerse/Code");
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
            String classpath = System.getProperty("user.dir") + "/data";
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
                log.error("Read file failed, path={}", filePath, e);
            }
            request.setAttribute("fileId", file.getFileId());
            request.setAttribute("lang", fileType);
            request.setAttribute("codeContent", sb.toString());
        }
        return "InsightfulVerse/Code";
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/Code.run")
    public String ivXCode(HttpServletRequest request) {
        String code = request.getParameter("code");
        String lang = request.getParameter("lang");
        log.info("Handle request: POST /InsightfulVerse/Code.run, Lang: {}", lang);
        String result;
        try {
            result = codeService.runCode(code, lang);
        } catch (Exception e) {
            log.error("Code exec failed, code={}, lang={}", code, lang, e);
            request.getSession().setAttribute("errorMessage", e.getMessage());
            return "redirect:/InsightfulVerse/Error";
        }
        return result;
    }

    @RequestMapping("/InsightfulVerse/Course")
    public String ivCourse(HttpServletRequest request) {
        log.info("Handle request: GET /InsightfulVerse/Course");
        User user = (User) request.getSession().getAttribute("user");
        Long userId = null;
        if (user != null) userId = user.getUserId();
        List<CourseVO> courseList = courseService.allCourseVO(userId);
        request.setAttribute("courseList", courseList);
        return "InsightfulVerse/Course";
    }

    @RequestMapping("/InsightfulVerse/CourseInfo")
    public String ivCourseInfo(HttpServletRequest request) {
        Long courseId = Long.valueOf(request.getParameter("courseId"));
        log.info("Handle request: GET /InsightfulVerse/CourseInfo?{}", courseId);
        User user = (User) request.getSession().getAttribute("user");
        Long userId = null;
        if (user != null) {
            userId = user.getUserId();
            if (user.getUserId() != null && user.getAuthority() == "infinite" &&
                    (boolean) request.getSession().getAttribute("authorize")) {
                request.getSession().setAttribute("lastUrl", request.getRequestURL());
                return "redirect:/InsightfulVerse/VerifyPerm";
            }
        }
        List<FileVO> sourceList = fileService.getCourseFilesVO(userId, courseId);
        request.setAttribute("sourceList", sourceList);
        return "InsightfulVerse/CourseInfo";
    }

    @RequestMapping("/InsightfulVerse/Error")
    public String error(HttpServletRequest request) {
        Object errMsg = request.getSession().getAttribute("errorMessage");
        log.info("Handle request: GET /InsightfulVerse/Error, Cause: {}", errMsg);
        request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
        request.getSession().setAttribute("errorMessage", null);
        return "InsightfulVerse/Error";
    }

    @RequestMapping("/InsightfulVerse/Login")
    public String ivLogin() {
        log.info("Handle request: GET /InsightfulVerse/Login");
        return "InsightfulVerse/Login";
    }

    @RequestMapping("/InsightfulVerse/Login.do")
    public String ivXLogin(HttpServletRequest request, HttpServletResponse response) {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");
        log.info("Handle request: POST /InsightfulVerse/Login.do, identifier: {}", identifier);
        User user = userService.login(identifier, password);
        if (user.getUserId() == null) {
            request.getSession().setAttribute("errorMessage", "Login failed, please check your username and password and try again.");
            return "redirect:/InsightfulVerse/Error";
        } else {
            String ip = geoAccessService.getClientIp(request);

/*
            log.debug(geoAccessService.setGeoAccess("131.38.173.92", 10000000000L).toString()); // DOD
            log.debug(geoAccessService.setGeoAccess("131.46.237.185", 10000000000L).toString()); // AF
            log.debug(geoAccessService.setGeoAccess("65.248.36.175", 10000000000L).toString()); // X
            log.debug(geoAccessService.setGeoAccess("104.134.196.157", 10000000000L).toString()); // GO
            log.debug(geoAccessService.setGeoAccess("165.204.52.173", 10000000000L).toString()); // AMD
            log.debug(geoAccessService.setGeoAccess("174.193.53.204", 10000000000L).toString()); // V
*/

            if (user.getUserId() != 1000000000L) {
                GeoAccess geo = geoAccessService.setGeoAccess(ip, user.getUserId());
                log.info("User {} login at {}", geo.getUserId(), geo.getBaseInfo());
            }

            request.getSession().setAttribute("user", user);
            return "redirect:/InsightfulVerse/index";
        }
    }

    @RequestMapping("/InsightfulVerse/Logout")
    public String ivLogout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Handle request: POST /InsightfulVerse/Logout, userId: {}",
                ((User) request.getSession().getAttribute("user")).getUserId());
        request.getSession().removeAttribute("user");
        userService.logout(request, response);
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/Personal")
    public String ivPersonal(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/InsightfulVerse/Login";
        }
        List<ViewRecord> viewRecordList = viewRecordService.recentViewedFile(user.getUserId());
        request.setAttribute("viewRecordList", viewRecordList);
        request.setAttribute("ivVersion", iVVersionService.getLatestVersion());
        request.setAttribute("geoAccessList", geoAccessService.getGeoAccessByUser(user.getUserId()).stream().limit(5).toList());
        return "InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/Player")
    public String ivPlayer(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        log.info("Handle request: GET /InsightfulVerse/Player, request media: {}", file.getFileId());
        request.setAttribute("file", file);
        switch (file.getType().toLowerCase()) {
            case "wav", "wma", "mp3", "flac", "m4a" -> {
                AudioInfoVO audioInfoVO = AudioTools.parseAudioMetadata(file.getFilePath());
                if (audioInfoVO != null) {
                    request.setAttribute("audio", audioInfoVO);
                }
                return "InsightfulVerse/Player/AudioPlayer";
            }
            default -> {
                return "InsightfulVerse/Player/VideoPlayer";
            }
        }
    }

    @RequestMapping("/InsightfulVerse/Reader")
    public String ivReader(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        log.info("Handle request: GET /InsightfulVerse/Reader, request document: {}", file.getFileId());
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
    public String ivPainter(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        log.info("Handle request: GET /InsightfulVerse/Reader, request picture: {}", file.getFileId());
        request.setAttribute("file", file);
        return "/InsightfulVerse/Painter";
    }

    @RequestMapping("/InsightfulVerse/Register")
    public String ivRegister() {
        log.info("Handle request: GET /InsightfulVerse/Register");
        return "InsightfulVerse/Register";
    }

    @RequestMapping("/InsightfulVerse/Register.do")
    public String ivxRegister(HttpServletRequest request, HttpServletResponse response) {
        String message = userService.register(request, response);
        log.info("Handle request: POST /InsightfulVerse/Register.do");
        if (message == null) {
            return "redirect:/InsightfulVerse/index";
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
    }

    @RequestMapping("/InsightfulVerse/DelAccount")
    public String ivDelAccount() {
        log.info("Handle request: GET /InsightfulVerse/DelAccount");
        return "InsightfulVerse/DelAccount";
    }

    @RequestMapping("/InsightfulVerse/DelAccount.do")
    public String ivxDelAccount(HttpServletRequest request) {
        String message = userService.deleteAccount(request);
        log.info("Handle request: POST /InsightfulVerse/DelAccount.do");
        if (message != null) {
            request.getSession().setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
        request.getSession().removeAttribute("user");
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/UpdateUser")
    public String ivUpdateUser(HttpServletRequest request) {
        User user = userService.userinfo(getIdentifierSession(request));
        log.info("Handle request: GET /InsightfulVerse/UpdateUser");
        if (user == null) {
            request.getSession().setAttribute("errorMessage", "Please log in first.");
            return "redirect:/InsightfulVerse/Error";
        }
        request.setAttribute("user", user);
        return "InsightfulVerse/UpdateInfo";
    }

    private String getIdentifierSession(HttpServletRequest request) {
        String identifier = null;
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            identifier = String.valueOf(user.getUserId());
        }
        if (identifier == null) {
            identifier = request.getParameter("userId");
        }
        if (identifier == null) {
            identifier = request.getParameter("identifier");
        }
        return identifier;
    }

    @RequestMapping("/InsightfulVerse/UpdateUser.do")
    public String ivxUpdateUser(HttpServletRequest request) {
        String identifier = getIdentifierSession(request);
        log.info("Handle request: POST /InsightfulVerse/UpdateUser.do, user identifier: {}", identifier);
        String message = userService.updateUser(request);
        if (message != null) {
            request.getSession().setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
        User user = userService.userinfo(identifier);
        request.getSession().setAttribute("user", user);
        return "redirect:/InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/NihilityZone")
    public String ivNihilityZone(HttpServletRequest request) {
        log.info("Handle request: REQUEST /InsightfulVerse/NihilityZone, user identifier: {}", getIdentifierSession(request));
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

    @RequestMapping("/InsightfulVerse/Halo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> ivHalo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null) {
            response.put("status", "unauthorized");
            response.put("redirectUrl", "/InsightfulVerse/VerifyPerm");
            return ResponseEntity.status(401).body(response);
        } else if (!checkPermission(request)) {
            response.put("status", "forbidden");
            response.put("message", "Insufficient permissions");
            response.put("redirectUrl", "/InsightfulVerse/Personal");
            return ResponseEntity.status(403).body(response);
        } else {
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
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/VerifyCode")
    public String ivVerifyCode(HttpServletRequest request) {
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
    public String ivUpdFile(HttpServletRequest request, HttpServletResponse response) {
        String courseId = request.getParameter("courseId");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null || (!user.getAuthority().equals("admin") && !user.getAuthority().equals("infinite"))) {
            return "redirect:" + request.getHeader("Referer");
        }
        File file = fileService.updFile(request, response);
        return "redirect:/InsightfulVerse/CourseInfo?courseId=" + courseId + "#sc" + file.getFileId();
    }

    @RequestMapping("/InsightfulVerse/DelFile")
    public String ivDelFile(HttpServletRequest request) {
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
    public String ivUpdCourse(@RequestParam(required = false) String courseId,
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
        course.setCourseId(courseService.updateCourse(course).getCourseId());
        return "redirect:/InsightfulVerse/Course#cs" + course.getCourseId();
    }

    @RequestMapping("/InsightfulVerse/DelCourse")
    public String ivDelCourse(@RequestParam Long courseId, HttpServletRequest request) {
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
    public String ivVerifyPerm(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !user.getAuthority().equals("infinite")) {
            return "redirect:/InsightfulVerse/";
        }
        return "/InsightfulVerse/VerifyPermissions";
    }

    @RequestMapping("/InsightfulVerse/VerifyPerm.do")
    public String ivxVerifyPerm(HttpServletRequest request, HttpServletResponse response) {
        User user = userService.verifyPerm(request, response);
        if (user != null && user.getAuthority().equals("infinite")) {
            request.getSession().setAttribute("authorize", true);
        } else {
            return "redirect:/InsightfulVerse/index";
        }
        return "redirect:/InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/ViewHistory")
    public String ivViewHistory(HttpServletRequest request) {
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
    public String search(HttpServletRequest request) {
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
            collectionService.updateCollItem(user.getUserId(), id, type);
        }

        if ("source".equalsIgnoreCase(type)) {
            File file = fileService.getFileById(id);
            return "redirect:/InsightfulVerse/CourseInfo?courseId=" + file.getCourseId() + "#sc" + id;
        } else {
            if (keyword != null && !keyword.isEmpty()) {
                return "redirect:/InsightfulVerse/Course#cs" + id;
            }
            return "redirect:/InsightfulVerse/Course";
        }
    }

    @RequestMapping("/InsightfulVerse/Version")
    public String version(HttpServletRequest request) {
        List<Version> versionList = iVVersionService.getAllVersion();
        request.setAttribute("versionList", versionList);
        return "InsightfulVerse/Version";
    }

    @RequestMapping("/InsightfulVerse/GeoAccess")
    public String geoAccess(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/InsightfulVerse/Login";
        }
        request.setAttribute("geoAccessList", geoAccessService.getGeoAccessByUser(user.getUserId()));
        return "InsightfulVerse/GeoAccess";
    }

    @RequestMapping("/InsightfulVerse/AiBot")
    public String aiBot(HttpServletRequest request) {
        return "InsightfulVerse/AiBot";
    }

    private boolean checkPermission(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (
            user != null
            && user.getUserId() != null
            && "infinite".equals(user.getAuthority())
            && Boolean.TRUE.equals(request.getSession().getAttribute("authorize"))
        ) {
            request.getSession().setAttribute("lastUrl", request.getRequestURL());
            request.getSession().setAttribute("lastUrl", request.getRequestURL());
            return true;
        }
        return false;
    }
}
