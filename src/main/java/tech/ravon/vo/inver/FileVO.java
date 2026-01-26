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

package tech.ravon.vo.inver;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FileVO implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long fileId;

    private String fileName;

    private String type;

    private Long courseId;

    private String remarks;

    private Long uploadUser;

    private Date uploadDate;

    private String filePath;

    private Integer isFavorite;

    private Date favoriteTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public String getThumb() {
        if (this.filePath == null || this.filePath.isEmpty()) return "/img/none.jpg";

        String p = this.filePath;
        int d = p.lastIndexOf('.');
        return (d != -1 ? p.substring(0, d) : p) + "-Thumbnail.jpg";
    }

    public boolean getHasFavorited() {
        return isFavorite != null && isFavorite == 1;
    }
}