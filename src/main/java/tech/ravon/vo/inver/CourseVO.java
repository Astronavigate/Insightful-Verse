package tech.ravon.vo.inver;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


@Data
public class CourseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long courseId;

    private String courseName;

    private String courseInfo;

    private Integer isFavorite;

    private Date favoriteTime;

    public boolean getHasFavorited() {
        return isFavorite != null && isFavorite == 1;
    }
}