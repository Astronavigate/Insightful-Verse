package tech.ravon.service.iviep;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import tech.ravon.model.iviep.File;

import java.util.List;

@Service
public interface FileService {

    String getFile(HttpServletRequest request, HttpServletResponse response);

    File getFileById(String fileId);

    List<File> getCourseFiles(String courseId);

    void deleteFile(String fileId);

    void deleteCourseFiles(String courseId);

    void updFile(HttpServletRequest request, HttpServletResponse response);

    List<File> getFileByName(String keyword);

}
