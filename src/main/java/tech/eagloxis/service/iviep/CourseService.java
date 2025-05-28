package tech.eagloxis.service.iviep;

import org.springframework.stereotype.Service;
import tech.eagloxis.model.iviep.Course;

import java.util.List;

@Service
public interface CourseService {

    List<Course> allCourse();

    void deleteCourse(String courseId);

    void updateCourse(Course course);

    List<Course> getCourseByName(String keyword);

}
