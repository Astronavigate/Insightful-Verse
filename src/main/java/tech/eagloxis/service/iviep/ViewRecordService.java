package tech.eagloxis.service.iviep;

import jakarta.servlet.http.HttpServletRequest;
import tech.eagloxis.model.iviep.ViewRecord;

import java.util.List;

public interface ViewRecordService {

    List<ViewRecord> recentViewedFile(String userId);

    List<ViewRecord> viewedFile(String userId);

    void saveViewRecord(HttpServletRequest request);

    void delRecordByFileId(String fileId);

    int delRecordByUserId(String userId);

}
