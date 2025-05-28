package tech.eagloxis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tech.eagloxis.model.iviep.ViewRecord;

import java.util.Date;
import java.util.List;

@Mapper
public interface ViewRecordDao extends BaseMapper<ViewRecord> {

    List<ViewRecord> getNewestViewRecords(String userId);

    List<ViewRecord> getViewRecords(String userId);

    ViewRecord getViewHistory(String userId, String fileId);

    void setViewRecords(String fileId, String userId);

    void setViewRecords(String fileId, String userId, Date duration);

    void updateViewRecords(String recordId);

    void updateViewRecordsWithDuration(String recordId, String userId, Date duration);

    void delRecordByFileId(String fileId);

    int delRecordByUserId(String userId);
}
