package tech.ravon.service.iviep.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.ViewRecordDao;
import tech.ravon.model.iviep.User;
import tech.ravon.model.iviep.ViewRecord;
import tech.ravon.service.iviep.FileService;
import tech.ravon.service.iviep.ViewRecordService;

import java.util.List;

@Service
public class ViewRecordServiceImpl implements ViewRecordService {

    @Autowired
    ViewRecordDao viewRecordDao;

    @Autowired
    FileService fileService;


    @Override
    public List<ViewRecord> recentViewedFile(String userId) {
        List<ViewRecord> viewRecordList = viewRecordDao.getNewestViewRecords(userId);
        System.out.println("List size " + viewRecordList.size());
        for (ViewRecord viewRecord : viewRecordList) {
            viewRecord.setFile(fileService.getFileById(String.valueOf(viewRecord.getFileId())));
        }
        return viewRecordList;
    }

    @Override
    public List<ViewRecord> viewedFile(String userId) {
        List<ViewRecord> viewRecordList = viewRecordDao.getViewRecords(userId);
        for (ViewRecord viewRecord : viewRecordList) {
            viewRecord.setFile(fileService.getFileById(String.valueOf(viewRecord.getFileId())));
        }
        return viewRecordList;
    }

    @Override
    public void saveViewRecord(HttpServletRequest request) {
        String fileId = request.getParameter("fileId");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null) {
            return;
        }
        ViewRecord vr = viewRecordDao.getViewHistory(user.getUserId().toString(), fileId);
        if (vr == null) {
            viewRecordDao.setViewRecords(fileId, String.valueOf(user.getUserId()));
        } else {
            viewRecordDao.updateViewRecords(vr.getRecordId().toString());
        }
    }

    @Override
    public void delRecordByFileId(String fileId) {
        viewRecordDao.delRecordByFileId(fileId);
    }

    @Override
    public int delRecordByUserId(String userId) {
        return viewRecordDao.delRecordByUserId(userId);
    }
}
