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
import org.springframework.beans.factory.annotation.Autowired;
import tech.ravon.mapper.CourseDao;
import tech.ravon.mapper.FileDao;
import tech.ravon.model.inver.Course;
import org.springframework.stereotype.Service;
import tech.ravon.model.inver.File;
import tech.ravon.model.inver.User;
import tech.ravon.service.inver.CourseService;
import tech.ravon.service.inver.FileService;
import tech.ravon.service.inver.ViewRecordService;
import tech.ravon.vo.inver.CourseVO;

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
        List<Course> courseList = courseDao.getAllCourse();
        return courseList;
    }

    @Override
    public List<CourseVO> allCourseVO(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Long userId = null;
        if (user != null) userId = user.getUserId();
        List<CourseVO> courseVOList = courseDao.getAllCourseVO(userId);
        return courseVOList;
    }

    @Override
    public void deleteCourse(HttpServletRequest request, Long courseId) {
        List<File> files = fileService.getCourseFiles(courseId);
        for (File file : files) {
            viewRecordService.delRecordByFileId(file.getFileId());
        }
        fileService.deleteCourseFiles(request, courseId);
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
