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

package tech.ravon.service.inver.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.ViewRecordDao;
import tech.ravon.model.inver.User;
import tech.ravon.model.inver.ViewRecord;
import tech.ravon.service.inver.FileService;
import tech.ravon.service.inver.ViewRecordService;

import java.util.List;

@Service
public class ViewRecordServiceImpl implements ViewRecordService {

    private final ViewRecordDao viewRecordDao;
    private final FileService fileService;

    public ViewRecordServiceImpl(
            ViewRecordDao viewRecordDao,
            @Lazy FileService fileService) {
        this.viewRecordDao = viewRecordDao;
        this.fileService = fileService;
    }


    @Override
    public List<ViewRecord> recentViewedFile(Long userId) {
        List<ViewRecord> viewRecordList = viewRecordDao.getNewestViewRecords(userId);
        System.out.println("List size " + viewRecordList.size());
        for (ViewRecord viewRecord : viewRecordList) {
            viewRecord.setFile(fileService.getFileById(viewRecord.getFileId()));
        }
        return viewRecordList;
    }

    @Override
    public List<ViewRecord> viewedFile(Long userId) {
        List<ViewRecord> viewRecordList = viewRecordDao.getViewRecords(userId);
        for (ViewRecord viewRecord : viewRecordList) {
            viewRecord.setFile(fileService.getFileById(viewRecord.getFileId()));
        }
        return viewRecordList;
    }

    @Override
    public void saveViewRecord(HttpServletRequest request) {
        Long fileId = Long.parseLong(request.getParameter("fileId"));
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null) {
            return;
        }
        ViewRecord vr = viewRecordDao.getViewHistory(user.getUserId(), fileId);
        if (vr == null) {
            viewRecordDao.setViewRecords(fileId, user.getUserId());
        } else {
            viewRecordDao.updateViewRecords(vr.getRecordId());
        }
    }

    @Override
    public void delRecordByFileId(Long fileId) {
        viewRecordDao.delRecordByFileId(fileId);
    }

    @Override
    public int delRecordByUserId(Long userId) {
        return viewRecordDao.delRecordByUserId(userId);
    }
}
