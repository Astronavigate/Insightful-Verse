package tech.eagloxis.model.iviep;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@TableName(value ="fileviewrecords")
@Data
public class ViewRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer recordId;

    private Integer fileId;

    private Integer userId;

    private Integer viewDuration;

    private Date viewDate;

    private Date firstView;

    private Integer viewCount;

    @TableField(exist = false)
    private File file;

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
        ViewRecord other = (ViewRecord) that;
        return (this.getRecordId() == null ? other.getRecordId() == null : this.getRecordId().equals(other.getRecordId()))
            && (this.getFileId() == null ? other.getFileId() == null : this.getFileId().equals(other.getFileId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getViewDuration() == null ? other.getViewDuration() == null : this.getViewDuration().equals(other.getViewDuration()))
            && (this.getViewDate() == null ? other.getViewDate() == null : this.getViewDate().equals(other.getViewDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRecordId() == null) ? 0 : getRecordId().hashCode());
        result = prime * result + ((getFileId() == null) ? 0 : getFileId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getViewDuration() == null) ? 0 : getViewDuration().hashCode());
        result = prime * result + ((getViewDate() == null) ? 0 : getViewDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", recordId=").append(recordId);
        sb.append(", fileId=").append(fileId);
        sb.append(", userId=").append(userId);
        sb.append(", viewDuration=").append(viewDuration);
        sb.append(", viewDate=").append(viewDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}