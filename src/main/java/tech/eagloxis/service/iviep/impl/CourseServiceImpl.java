package tech.eagloxis.service.iviep.impl;

import org.springframework.beans.factory.annotation.Autowired;
import tech.eagloxis.mapper.CourseDao;
import tech.eagloxis.mapper.FileDao;
import tech.eagloxis.model.iviep.Course;
import org.springframework.stereotype.Service;
import tech.eagloxis.model.iviep.File;
import tech.eagloxis.service.iviep.CourseService;
import tech.eagloxis.service.iviep.FileService;
import tech.eagloxis.service.iviep.ViewRecordService;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private FileDao fileDao;

    @Autowired
    FileService fileService;

    @Autowired
    ViewRecordService viewRecordService;

    @Override
    public List<Course> allCourse() {

        fileDao.getFilesByCourse("1000000");
        List<Course> all = courseDao.getAllCourse();
        System.out.println(all.size());
        System.out.println(all.get(0));
        List<Course> courseList = courseDao.getAllCourse();
        return courseList;
    }

    @Override
    public void deleteCourse(String courseId) {
        List<File> files = fileService.getCourseFiles(courseId);
        for (File file : files) {
            viewRecordService.delRecordByFileId(String.valueOf(file.getFileId()));
        }
        fileService.deleteCourseFiles(courseId);
        courseDao.deleteCourse(courseId);
    }

    @Override
    public void updateCourse(Course course) {
        courseDao.updateCourse(course);
    }

    @Override
    public List<Course> getCourseByName(String keyword) {
        return courseDao.getCourseByName(keyword);
    }
}
