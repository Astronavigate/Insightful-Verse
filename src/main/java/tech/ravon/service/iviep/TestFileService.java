package tech.ravon.service.iviep;

import tech.ravon.model.iviep.File;

import java.util.List;

public interface TestFileService {

    List<File> getCourseFiles(String courseId);
}
