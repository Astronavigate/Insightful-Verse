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

package tech.ravon.service.inver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import tech.ravon.model.inver.File;

import java.util.List;

@Service
public interface FileService {

    String getFile(HttpServletRequest request, HttpServletResponse response);

    File getFileById(Long fileId);

    List<File> getCourseFiles(Long courseId);

    void deleteFile(HttpServletRequest request, Long fileId);

    void deleteCourseFiles(HttpServletRequest request, Long courseId);

    void updFile(HttpServletRequest request, HttpServletResponse response);

    List<File> getFileByName(String keyword);

}
