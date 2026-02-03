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

package tech.ravon.service.inver.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import tech.ravon.mapper.CourseDao;
import tech.ravon.model.inver.Course;
import org.springframework.stereotype.Service;
import tech.ravon.model.inver.File;
import tech.ravon.service.VectorService;
import tech.ravon.service.inver.CourseService;
import tech.ravon.service.inver.FileService;
import tech.ravon.service.inver.ViewRecordService;
import tech.ravon.vo.inver.CourseVO;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;

    private final FileService fileService;

    private final ViewRecordService viewRecordService;

    private final VectorService vectorService;

    @Override
    public List<Course> allCourse() {
        return courseDao.getAllCourse();
    }

    @Override
    public List<CourseVO> allCourseVO(Long userId) {
        return courseDao.getAllCourseVO(userId);
    }

    @Override
    public void deleteCourse(HttpServletRequest request, Long courseId) {
        List<File> files = fileService.getCourseFiles(courseId);
        for (File file : files) {
            viewRecordService.delRecordByFileId(file.getFileId());
        }
        fileService.deleteCourseFiles(request, courseId);
        courseDao.deleteCourse(courseId);
        vectorService.delVector(String.valueOf(courseId), "inver.courses");
    }

    @Override
    public void updateCourse(Course course) {
        courseDao.updateCourse(course);
        course = courseDao.getCourseByInfo(course);
        String fileHref = "/InsightfulVerse/CourseInfo?courseId=" + course.getCourseId();
        String contentForVector = "CourseName: " + course.getCourseName() + "\nRemark: " + course.getCourseInfo() + "\nHref: " + fileHref;
        vectorService.updVector(String.valueOf(course.getCourseId()), "inver.courses", contentForVector);
    }

    @Override
    public List<Course> getCourseByName(String keyword) {
        return courseDao.getCourseByName(keyword);
    }
}
