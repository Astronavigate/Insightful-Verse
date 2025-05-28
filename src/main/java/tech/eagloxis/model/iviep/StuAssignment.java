package tech.eagloxis.model.iviep;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@TableName(value ="stuassignments")
@Data
public class StuAssignment implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer submissionId;

    private Integer testId;

    private Integer userId;

    private Date submissionDate;

    private Integer grade;

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
        StuAssignment other = (StuAssignment) that;
        return (this.getSubmissionId() == null ? other.getSubmissionId() == null : this.getSubmissionId().equals(other.getSubmissionId()))
            && (this.getTestId() == null ? other.getTestId() == null : this.getTestId().equals(other.getTestId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getSubmissionDate() == null ? other.getSubmissionDate() == null : this.getSubmissionDate().equals(other.getSubmissionDate()))
            && (this.getGrade() == null ? other.getGrade() == null : this.getGrade().equals(other.getGrade()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSubmissionId() == null) ? 0 : getSubmissionId().hashCode());
        result = prime * result + ((getTestId() == null) ? 0 : getTestId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getSubmissionDate() == null) ? 0 : getSubmissionDate().hashCode());
        result = prime * result + ((getGrade() == null) ? 0 : getGrade().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", submissionId=").append(submissionId);
        sb.append(", testId=").append(testId);
        sb.append(", userId=").append(userId);
        sb.append(", submissionDate=").append(submissionDate);
        sb.append(", grade=").append(grade);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}