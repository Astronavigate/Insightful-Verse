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

import com.fasterxml.uuid.Generators;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.FileDao;
import tech.ravon.model.iviep.File;
import tech.ravon.model.iviep.User;
import tech.ravon.service.iviep.FileService;
import tech.ravon.service.iviep.ViewRecordService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileDao fileDao;

    @Lazy
    @Autowired
    ViewRecordService viewRecordService;

    // 配置上传参数
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 16;  // 16MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 248; // 248MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 256; // 256MB

    @Override
    public String getFile(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    @Override
    public File getFileById(String fileId) {
        return fileDao.getFileById(fileId);
    }

    @Override
    public List<File> getCourseFiles(String courseId) {
        return fileDao.getFilesByCourse(courseId);
    }

    @Override
    public void deleteFile(String fileId) {
        // 获取文件信息
        File file = fileDao.getFileById(fileId);
        if (file == null) {
            // 如果文件未找到，直接返回或抛出异常
            throw new IllegalArgumentException("File with ID " + fileId + " does not exist.");
        }

        String filePath = file.getFilePath();

        // 获取当前工作目录的路径
        String baseDirectory = Paths.get(System.getProperty("user.dir"), "src", "main", "resources").toString();

        // 构建文件的完整路径
        Path fileToDelete = Paths.get(baseDirectory, "/static" ,filePath);

        try {
            // 删除文件
            Files.delete(fileToDelete);
            System.out.println("File deleted successfully: " + filePath);
        } catch (IOException e) {
            // 处理文件删除异常
            System.err.println("Error deleting file: " + filePath);
            e.printStackTrace();
        }

        // 删除相关的记录
        try {
            // 删除与文件相关的查看记录
            viewRecordService.delRecordByFileId(fileId);
            System.out.println("View records deleted for file ID: " + fileId);
        } catch (Exception e) {
            // 处理删除查看记录时的异常
            System.err.println("Error deleting view records for file ID: " + fileId);
            e.printStackTrace();
        }

        // 删除文件记录
        try {
            // 从数据库中删除文件记录
            fileDao.deleteFile(fileId);
            System.out.println("File record deleted from database for file ID: " + fileId);
        } catch (Exception e) {
            // 处理删除数据库记录时的异常
            System.err.println("Error deleting file record from database for file ID: " + fileId);
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCourseFiles(String courseId) {
        List<File> files = fileDao.getFilesByCourse(courseId);
        for (File file : files) {
            deleteFile(String.valueOf(file.getFileId()));
        }
    }

    @Override
    public void updFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("开始处理文件上传 (UUID v7 & 逻辑优化)");

            // --- 1. 权限和参数获取 ---
            String userId = null;
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                System.out.println("用户未登录");
                return;
            }
            userId = String.valueOf(user.getUserId());

            String remark = request.getParameter("fileRemark");
            String fileId = request.getParameter("fileId");
            String name = request.getParameter("fileName");
            String courseId = request.getParameter("courseId");

            // --- 2. 文件和字幕 Part 获取 ---
            Part filePart = request.getPart("file");
            String submittedMediaFileName = (filePart != null) ? filePart.getSubmittedFileName() : null;

            Part subtitlePart = request.getPart("subtitleFile");
            String submittedSubtitleFileName = (subtitlePart != null) ? subtitlePart.getSubmittedFileName() : null;

            boolean isNewFileUpload = (submittedMediaFileName != null && !submittedMediaFileName.isEmpty());
            boolean isSubtitleUpload = (submittedSubtitleFileName != null && !submittedSubtitleFileName.isEmpty());

            if (!isNewFileUpload && !isSubtitleUpload && (fileId == null || fileId.isEmpty())) {
                System.out.println("没有上传文件且文件 ID 为空，操作取消");
                return;
            }

            // --- 3. 初始化变量 ---
            String mediaExtension = "";
            String baseFileName = null; // UUID v7 字符串
            String insertDirectory = "/media/";
            File oldFile = null;

            // --- 4. 获取旧文件信息 ---
            if (fileId != null && !fileId.isEmpty()) {
                oldFile = fileDao.getFileById(fileId);
            }

            // --- 5. 确定 baseFileName (核心逻辑) ---
            if (isNewFileUpload) {
                // 情况 A: 上传了新媒体文件 -> 生成新的 UUID v7
                baseFileName = Generators.timeBasedEpochGenerator().generate().toString();
                System.out.println("上传新文件，生成新 UUID v7: " + baseFileName);

            } else if (oldFile != null) {
                // 情况 B/C: 仅更新信息 或 仅更新字幕 -> 沿用旧的 UUID
                String oldPath = oldFile.getFilePath();
                if (oldPath != null) {
                    String oldFileNameWithExt = Paths.get(oldPath).getFileName().toString();
                    int lastDot = oldFileNameWithExt.lastIndexOf('.');
                    if (lastDot > 0) {
                        baseFileName = oldFileNameWithExt.substring(0, lastDot);
                    }
                }
            }

            if (baseFileName == null && (isNewFileUpload || isSubtitleUpload)) {
                System.out.println("无法确定 baseFileName，操作失败");
                return;
            }

            // --- 6. 确定存储路径 ---
            String uploadDirectory = "static/media/";
            Path staticPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", uploadDirectory);
            if (!Files.exists(staticPath)) {
                Files.createDirectories(staticPath);
                System.out.println("创建目录: " + staticPath);
            }

            // --- 7. 保存媒体文件 (如果上传了) ---
            if (isNewFileUpload) {
                int dotIndex = submittedMediaFileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    mediaExtension = submittedMediaFileName.substring(dotIndex + 1);
                }
                Path mediaTargetPath = staticPath.resolve(baseFileName + "." + mediaExtension);
                try (InputStream fileContent = filePart.getInputStream()) {
                    Files.copy(fileContent, mediaTargetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("媒体文件已成功保存: " + mediaTargetPath);
                }
            }

            // --- 8. 保存字幕文件 (如果上传了) ---
            if (isSubtitleUpload) {
                int dotIndex = submittedSubtitleFileName.lastIndexOf('.');
                String subtitleExtension = (dotIndex > 0) ? submittedSubtitleFileName.substring(dotIndex + 1).toLowerCase() : null;

                if (subtitleExtension != null) {
                    // 字幕文件路径与媒体文件使用相同的 UUID 基础名
                    Path subtitleTargetPath = staticPath.resolve(baseFileName + "." + subtitleExtension);

                    try (InputStream subtitleContent = subtitlePart.getInputStream()) {
                        Files.copy(subtitleContent, subtitleTargetPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("字幕文件已成功保存: " + subtitleTargetPath);
                    }
                    // 【不保存路径到数据库，依赖推导】
                }
            }

            // --- 9. 清理旧文件 (根据操作类型区分删除) ---
            if (oldFile != null) {
                String oldFilePathString = oldFile.getFilePath();
                if (oldFilePathString != null) {
                    String oldBaseNameWithPrefix = oldFilePathString.substring(0, oldFilePathString.lastIndexOf('.'));
                    String oldBaseName = Paths.get(oldBaseNameWithPrefix).getFileName().toString();

                    // 1. 如果上传了新媒体文件 (isNewFileUpload == true)
                    if (isNewFileUpload) {
                        // 情况 A: 删除旧媒体文件
                        Path oldFilePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", oldFilePathString);
                        Files.deleteIfExists(oldFilePath);
                        System.out.println("情况 A: 删除旧媒体文件: " + oldFilePath);

                        // 情况 A: 删除旧字幕文件（新文件上传后，字幕自动清空）
                        Path oldLrcPath = staticPath.resolve(oldBaseName + ".lrc");
                        Path oldSrtPath = staticPath.resolve(oldBaseName + ".srt");
                        Files.deleteIfExists(oldLrcPath);
                        Files.deleteIfExists(oldSrtPath);
                        System.out.println("情况 A: 自动删除旧同名字幕文件 (LRC/SRT)");
                    }
                    // 2. 如果只上传了新字幕文件 (isSubtitleUpload == true 且 isNewFileUpload == false)
                    else if (isSubtitleUpload) {
                        // 情况 B: 新字幕文件已在步骤 8 中覆盖旧的同名文件。
                        // 无需额外删除旧媒体文件。
                        System.out.println("情况 B: 仅更新字幕，旧媒体文件和旧字幕文件已被覆盖或保留。");
                    }
                }
            }

            // --- 10. 构建和更新数据库记录 ---
            File file = new File();
            file.setCourseId(Long.valueOf(courseId));

            if (fileId == null || fileId.isEmpty()) {
                // A. 新增文件
                file.setFileName(name);
                file.setRemarks(remark);
                file.setType(mediaExtension);
                file.setFilePath(insertDirectory + baseFileName + "." + mediaExtension);

            } else {
                // B. 更新文件
                file.setFileId(Long.valueOf(fileId));
                file.setFileName(name);
                file.setRemarks(remark);

                if (isNewFileUpload) {
                    // 上传了新媒体文件，更新路径和类型
                    file.setFilePath(insertDirectory + baseFileName + "." + mediaExtension);
                    file.setType(mediaExtension);
                } else {
                    // 未上传新媒体文件（只更新信息或字幕），保留旧路径和类型
                    file.setFilePath(oldFile != null ? oldFile.getFilePath() : null);
                    file.setType(oldFile != null ? oldFile.getType() : null);
                }
            }

            // 添加或更新文件记录
            fileDao.addFile(file, userId);
            System.out.println("文件记录更新成功，文件ID: " + file.getFileId());

        } catch (IOException | NumberFormatException e) {
            System.out.println("文件上传过程中发生IO或参数格式异常");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("文件上传过程中发生未知异常");
            e.printStackTrace();
        }
    }

    @Override
    public List<File> getFileByName(String keyword) {
        return fileDao.getFileByName(keyword);
    }
}
