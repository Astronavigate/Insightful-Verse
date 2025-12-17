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
import org.apache.ibatis.annotations.Param;
import tech.ravon.model.iviep.Annotation;

import java.util.List;

@Mapper
public interface AnnotationDao extends BaseMapper<Annotation> {

    /**
     * 获取某个用户在某一本书下的所有标注
     */
    List<Annotation> getAnnotationsByUserAndBook(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId
    );

    /**
     * 按类型获取标注（bookmark / highlight / note）
     */
    List<Annotation> getAnnotationsByType(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("type") String type
    );

    /**
     * 新增标注（如果你不用 MP 的 insert）
     */
    int insertAnnotation(Annotation annotation);

    /**
     * 更新标注内容 / 颜色 / 位置
     */
    int updateAnnotation(Annotation annotation);

    /**
     * 删除单条标注
     */
    int deleteAnnotation(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    /**
     * 删除某本书下某用户的所有标注（如重新导入）
     */
    int deleteAnnotationsByUserAndBook(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId
    );

    int deleteAnnotationsByBook(
            @Param("bookId") Long bookId
    );

    int deleteAnnotationsByUser(
            @Param("userId") Long userId
    );
}
