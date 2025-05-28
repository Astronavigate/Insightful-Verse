package tech.eagloxis.service.iviep;

import tech.eagloxis.model.iviep.File;

import java.util.List;

public interface TestFileService {

    List<File> getCourseFiles(String courseId);
}
