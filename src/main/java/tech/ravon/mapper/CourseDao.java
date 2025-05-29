package tech.ravon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.ravon.model.iviep.Course;

import java.util.List;

@Mapper
public interface CourseDao extends BaseMapper<Course> {

    List<Course> getAllCourse();

    void updateCourse(Course course);

    void deleteCourse(String courseId);

    List<Course> getCourseByName(String keyword);

}
