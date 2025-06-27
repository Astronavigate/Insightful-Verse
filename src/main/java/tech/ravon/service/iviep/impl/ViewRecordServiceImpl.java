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
