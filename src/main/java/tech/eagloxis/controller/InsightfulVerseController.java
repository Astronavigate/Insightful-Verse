package tech.eagloxis.controller;

import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.eagloxis.model.iviep.Course;
import tech.eagloxis.model.iviep.File;
import tech.eagloxis.model.iviep.User;
import tech.eagloxis.model.iviep.ViewRecord;
import tech.eagloxis.service.iviep.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    IVVersionService iVVersionService;
    @Autowired
    MailService mailService;
    @Autowired
    CodeService codeService;
    @Autowired
    PageService pageService;
    @Autowired
    HaloService haloService;

    @RequestMapping("/InsightfulVerse/")
    public String IVIEPMain() {
        return "InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/index")
    public String IVIEPIndex() {
        return "InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/About")
    public String IVIEPAbout() {
        return "InsightfulVerse/About";
    }

    @RequestMapping("/InsightfulVerse/Code")
    public String IVIEPCode(HttpServletRequest request) {
        File file = null;
        if (request.getSession().getAttribute("codeFile") != null) {
            file = (File) request.getSession().getAttribute("file");
            request.getSession().removeAttribute("codeFile");
        }
        if (file == null || file.getFilePath() == null || file.getFilePath().isEmpty()) {
            request.setAttribute("fileId", null);
            request.setAttribute("codeContent", "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, world!\");\n    }\n}");
        } else {
            String classpath = System.getProperty("user.dir") + "/src/main/resources/static";
            String filePath = classpath + file.getFilePath();

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
            request.setAttribute("codeContent", sb.toString());
        }
        return "InsightfulVerse/Code";
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/Code.java")
    public String IVIEPXCode(HttpServletRequest request, HttpServletResponse response) {
        String result;
        try {
            result = codeService.runJava(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", e.getMessage());
            return "redirect:/InsightfulVerse/Error";
        }
        return result;
    }

    @RequestMapping("/InsightfulVerse/Course")
    public String IVIEPCourse(HttpServletRequest request) {
        List<Course> courseList = courseService.allCourse();
        request.setAttribute("courseList", courseList);
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            if (user.getAuthority() == "infinite" && (boolean) request.getSession().getAttribute("authorize")) {
                request.getSession().setAttribute("lastUrl", request.getRequestURL());
                return "redirect:/InsightfulVerse/VerifyPerm";
            }
        }
        return "InsightfulVerse/Course";
    }

    @RequestMapping("/InsightfulVerse/CourseInfo")
    public String IVIEPCourseInfo(HttpServletRequest request) {
        String courseId = request.getParameter("courseId");
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
        return "InsightfulVerse/Error";
    }

    @RequestMapping("/InsightfulVerse/Login")
    public String IVIEPLogin() {
        return "InsightfulVerse/Login";
    }

    @RequestMapping("/InsightfulVerse/Login.do")
    public String IVIEPXLogin(HttpServletRequest request, HttpServletResponse response) {
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
    public String IVIEPLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute("user");
        userService.Logout(request, response);
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/Personal")
    public String IVIEPersonal(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/InsightfulVerse/Login";
        }
        List<ViewRecord> viewRecordList = viewRecordService.recentViewedFile(String.valueOf(user.getUserId()));
        request.setAttribute("viewRecordList", viewRecordList);
        request.setAttribute("ivVersion", iVVersionService.getLatestVersion().getVersion());
        return "InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/Player")
    public String IVIEPPlayer(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        request.setAttribute("file", file);
        System.out.println(file);
        return "/InsightfulVerse/Player";
    }

    @RequestMapping("/InsightfulVerse/Reader")
    public String IVIEPReader(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        request.setAttribute("file", file);
        return "InsightfulVerse/Reader";
    }

    @RequestMapping("/InsightfulVerse/Painter")
    public String IVIEPPainter(HttpServletRequest request) {
        File file = (File) request.getSession().getAttribute("file");
        request.setAttribute("file", file);
        return "/InsightfulVerse/Painter";
    }

    @RequestMapping("/InsightfulVerse/Register")
    public String IVIEPRegister() {
        return "InsightfulVerse/Register";
    }

    @RequestMapping("/InsightfulVerse/Register.do")
    public String IVIEPXRegister(HttpServletRequest request, HttpServletResponse response) {
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
    public String IVIEPUnregister() {
        return "InsightfulVerse/Unregister";
    }

    @RequestMapping("/InsightfulVerse/Unregister.do")
    public String IVIEPXUnregister(HttpServletRequest request, HttpServletResponse response) {
        String message = userService.Unregister(request, response);
        if (message != null) {
            request.getSession().setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
        request.getSession().removeAttribute("user");
        return "redirect:/InsightfulVerse/index";
    }

    @RequestMapping("/InsightfulVerse/UpdateUser")
    public String IVIEPUpdateUser(HttpServletRequest request) {
        User user = userService.Userinfo(request);
        request.setAttribute("user", user);
        return "InsightfulVerse/UpdateInfo";
    }

    @RequestMapping("/InsightfulVerse/UpdateUser.do")
    public String IVIEPXUpdateUser(HttpServletRequest request, HttpServletResponse response) {
        String message = userService.UpdateUser(request, response);
        System.out.println(message==null?"null-message":message);
        if (message != null) {
            request.getSession().setAttribute("errorMessage", message);
            return "redirect:/InsightfulVerse/Error";
        }
        User user = userService.Userinfo(request);
        request.getSession().setAttribute("user", user);
        return "redirect:/InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/NihilityZone")
    public String IVIEPNihilityZone(HttpServletRequest request) {
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
    public String IVIEPVerifyCode(HttpServletRequest request) {
        return mailService.getCaptcha(request);
    }

    @RequestMapping("/InsightfulVerse/File")
    public String IVIEPFile(HttpServletRequest request, HttpServletResponse response) {
        String fileId = request.getParameter("fileId");
        File file = fileService.getFileById(fileId);
        request.getSession().setAttribute("file", file);
        switch (file.getType().toLowerCase()) {
            case "pdf", "ppt", "xls", "xlsx", "doc", "docx" -> {
                return "redirect:/InsightfulVerse/Reader";
            }
            case "mp4", "mkv", "mov", "wmv", "wma", "mp3", "flac", "m4a" -> {
                return "redirect:/InsightfulVerse/Player";
            }
            case "jpg", "jpeg", "heif", "raw", "png", "gif", "webp" -> {
                return "redirect:/InsightfulVerse/Painter";
            }
            case "cpp", "py", "c", "java", "html", "css", "js", "jsp", "php", "aspx" -> {
                request.getSession().setAttribute("codeFile", true);
                return "redirect:/InsightfulVerse/Code";
            }
            default -> {
                return "../../" + file.getFilePath();
            }
        }
    }

    @ResponseBody
    @RequestMapping("/InsightfulVerse/Timer")
    public String IVIEPTimer(HttpServletRequest request) {
        System.out.println("User viewing file " + request.getParameter("fileId"));
        viewRecordService.saveViewRecord(request);
        return null;
    }

    @RequestMapping("/InsightfulVerse/UpdFile")
    public String IVIEPUpdFile(HttpServletRequest request, HttpServletResponse response) {
        String courseId = request.getParameter("courseId");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null || (!user.getAuthority().equals("admin") && !user.getAuthority().equals("infinite"))) {
            return "redirect:" + request.getHeader("Referer");
        }
        fileService.updFile(request, response);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping("/InsightfulVerse/DelFile")
    public String IVIEPDelFile(HttpServletRequest request) {
        String fileId = request.getParameter("fileId");
        String courseId = request.getParameter("courseId");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null || (!user.getAuthority().equals("infinite") && !user.getAuthority().equals("admin"))) {
            return "redirect:/InsightfulVerse/CourseInfo?courseId=" + courseId;
        }
        fileService.deleteFile(fileId);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping("/InsightfulVerse/UpdCourse")
    public String IVIEPUpdCourse(@RequestParam(required = false) String courseId,
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
            course.setCourseId(Integer.valueOf(courseId));
        }
        course.setCourseName(courseName);
        course.setCourseInfo(courseInfo);
        courseService.updateCourse(course);
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping("/InsightfulVerse/DelCourse")
    public String IVIEPDelCourse(@RequestParam String courseId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            if (user.getAuthority() == "infinite" && (boolean) request.getSession().getAttribute("authorize")) {
                request.getSession().setAttribute("lastUrl", "/InsightfulVerse/CourseInfo?courseId=" + courseId);
                return "redirect:/InsightfulVerse/VerifyPerm";
            }
        }
        courseService.deleteCourse(courseId);
        return "redirect:/InsightfulVerse/Course";
    }

    @RequestMapping("/InsightfulVerse/VerifyPerm")
    public String IVIEPVerifyPerm(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !user.getAuthority().equals("infinite")) {
            return "redirect:/InsightfulVerse/";
        }
        return "/InsightfulVerse/VerifyPermissions";
    }

    @RequestMapping("/InsightfulVerse/VerifyPerm.do")
    public String IVIEPXVerifyPerm(HttpServletRequest request, HttpServletResponse response) {
        User user = userService.verifyPerm(request, response);
        if (user != null && user.getAuthority().equals("infinite")) {
            request.getSession().setAttribute("authorize", true);
        } else {
            return "redirect:/InsightfulVerse/index";
        }
        return "redirect:/InsightfulVerse/Personal";
    }

    @RequestMapping("/InsightfulVerse/Halo")
    public String IVIEPHalo(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.getUserId() != null) {
            if (user.getAuthority().equals("infinite") && (request.getSession().getAttribute("authorize") != null && !(boolean) request.getSession().getAttribute("authorize"))) {
                request.getSession().setAttribute("lastUrl", "/InsightfulVerse/NihilityZone");
                return "redirect:/InsightfulVerse/VerifyPerm";
            } else if (user.getAuthority().equals("infinite") && (boolean) request.getSession().getAttribute("authorize")) {
                String code = request.getParameter("code");
                List<List<String>> resultList = haloService.runSQL(code);
                System.out.println(resultList);
                request.getSession().setAttribute("resultList", resultList);
                request.getSession().setAttribute("codeContent", code);
                return "redirect:/InsightfulVerse/NihilityZone";
            } else {
                return "redirect:/InsightfulVerse/Personal";
            }
        } else {
            return "redirect:/InsightfulVerse/Personal";
        }
    }

    @RequestMapping("/InsightfulVerse/ViewHistory")
    public String IVIEPViewHistory(HttpServletRequest request) {
        int max = request.getParameter("max") == null || request.getParameter("max").isEmpty() ? 15 : Integer.parseInt(request.getParameter("max"));
        int page = request.getParameter("page") == null || request.getParameter("page").isEmpty() ? 1 : Integer.parseInt(request.getParameter("page"));
        User user = (User) request.getSession().getAttribute("user");
        String userId = null;
        if (user != null) {
            userId = String.valueOf(user.getUserId());
        }
        if (userId == null) {
            request.getSession().setAttribute("errorMessage", "Please check your login statement and try again.");
            return "redirect:/InsightfulVerse/Error";
        }
        List<ViewRecord> viewRecordList = viewRecordService.viewedFile(userId);
        List<ViewRecord> pagedList = pageService.pageList(viewRecordList, page, max);
        int totalPages = (viewRecordList.size() + max - 1)/max;
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
}
