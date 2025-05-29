package tech.ravon.service.iviep.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.FileDao;
import tech.ravon.model.iviep.File;
import tech.ravon.service.iviep.TestFileService;

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
