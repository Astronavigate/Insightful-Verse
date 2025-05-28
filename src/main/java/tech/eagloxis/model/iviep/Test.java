package tech.eagloxis.model.iviep;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="test")
@Data
public class Test implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer testId;

    private String testName;

    private Integer courseId;

    private String testContent;

    private String testAnswer;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Test other = (Test) that;
        return (this.getTestId() == null ? other.getTestId() == null : this.getTestId().equals(other.getTestId()))
            && (this.getTestName() == null ? other.getTestName() == null : this.getTestName().equals(other.getTestName()))
            && (this.getCourseId() == null ? other.getCourseId() == null : this.getCourseId().equals(other.getCourseId()))
            && (this.getTestContent() == null ? other.getTestContent() == null : this.getTestContent().equals(other.getTestContent()))
            && (this.getTestAnswer() == null ? other.getTestAnswer() == null : this.getTestAnswer().equals(other.getTestAnswer()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTestId() == null) ? 0 : getTestId().hashCode());
        result = prime * result + ((getTestName() == null) ? 0 : getTestName().hashCode());
        result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
        result = prime * result + ((getTestContent() == null) ? 0 : getTestContent().hashCode());
        result = prime * result + ((getTestAnswer() == null) ? 0 : getTestAnswer().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", testId=").append(testId);
        sb.append(", testName=").append(testName);
        sb.append(", courseId=").append(courseId);
        sb.append(", testContent=").append(testContent);
        sb.append(", testAnswer=").append(testAnswer);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}