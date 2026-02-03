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

package tech.ravon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.ravon.model.inver.File;
import tech.ravon.vo.inver.FileVO;

import java.util.List;

@Mapper
public interface FileDao extends BaseMapper<File> {

    List<File> getAllFiles();

    List<File> getFilesByPop(Long limit);

    File getFileById(Long fileId);

    List<File> getFilesByCourse(Long courseId);

    List<FileVO> getFilesVOByCourse(Long userId, Long courseId);

    void updFile(File file, Long userId);

    void deleteFile(Long fileId);

    List<File> getFileByName(String keyword);

    File getFileByInfo(File file);

}
