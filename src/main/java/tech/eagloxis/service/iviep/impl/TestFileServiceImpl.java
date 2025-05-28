package tech.eagloxis.service.iviep.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.eagloxis.mapper.FileDao;
import tech.eagloxis.model.iviep.File;
import tech.eagloxis.service.iviep.TestFileService;

import java.util.List;

@Service
public class TestFileServiceImpl implements TestFileService {

    @Autowired
    private FileDao fileDao;

    @Override
    public List<File> getCourseFiles(String courseId) {
        return fileDao.getFilesByCourse(courseId);
    }

}
