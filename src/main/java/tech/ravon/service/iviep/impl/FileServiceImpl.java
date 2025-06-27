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
            System.out.println("开始处理文件上传");

            // 获取 Session 中的用户 ID
            String userId = null;
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) {
                System.out.println("用户未登录");
                return;
            }
            userId = String.valueOf(user.getUserId());
            System.out.println("获取到用户ID: " + userId);

            // 获取请求参数
            String remark = request.getParameter("fileRemark");
            String fileId = request.getParameter("fileId");
            String name = request.getParameter("fileName");
            String courseId = request.getParameter("courseId");

            String fileExtension = "";
            String fileName = "";
            String insertDirectory = "";
            File oldFile = null;

            // 获取文件部分
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    fileExtension = fileName.substring(dotIndex + 1);
                }

                // 确定上传和插入的目录
                String uploadDirectory = switch (fileExtension.toLowerCase()) {
                    case "pdf", "ppt", "xls", "xlsx", "doc", "docx" -> "static/doc/";
                    case "mp4", "mkv", "mov", "wmv", "wma", "mp3", "flac", "m4a" -> "static/media/";
                    case "jpg", "jpeg", "heif", "raw", "png", "gif", "webp" -> "static/image/";
                    case "cpp", "py", "c", "java", "html", "css", "js", "jsp", "php", "aspx" -> "static/code/";
                    default -> "static/ots/";
                };

                // 去掉 "static/" 前缀，供数据库保存使用
                insertDirectory = uploadDirectory.replaceFirst("^static", "");

                // 确保目录存在
                Path staticPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", uploadDirectory);
                if (!Files.exists(staticPath)) {
                    Files.createDirectories(staticPath);
                    System.out.println("创建目录: " + staticPath);
                }

                // 处理文件名重复的情况
                Path targetPath = staticPath.resolve(fileName);
                int counter = 1;
                while (Files.exists(targetPath)) {
                    // 如果文件存在，添加一个数字后缀
                    String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + counter + fileName.substring(fileName.lastIndexOf('.'));
                    targetPath = staticPath.resolve(newFileName);
                    counter++;
                    System.out.println("文件名已存在，修改为: " + targetPath.getFileName().toString());
                }

                // 保存文件到目标路径
                try (InputStream fileContent = filePart.getInputStream()) {
                    Files.copy(fileContent, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("文件已成功保存: " + targetPath);
                }

                fileName = targetPath.getFileName().toString(); // 更新 fileName 为新的（带有序号的）

            } else if (fileId == null || fileId.isEmpty()) {
                // 如果没有文件 ID，且没有上传文件，则直接返回
                System.out.println("没有上传文件且文件 ID 为空");
                return;
            }

            // 获取旧文件信息
            if (fileId != null && !fileId.isEmpty()) {
                oldFile = fileDao.getFileById(fileId);
                if (oldFile != null) {
                    System.out.println("找到旧文件: " + oldFile.getFileName());
                }
            }

            // 构建文件信息
            File file = new File();
            file.setCourseId(Integer.valueOf(courseId));
            if (fileId == null || fileId.isEmpty()) {
                // 新文件
                file.setFileName(name);
                file.setRemarks(remark);
                file.setType(fileExtension);
                file.setFilePath(insertDirectory + fileName);
                System.out.println("新增文件: " + fileName);
            } else {
                // 更新文件
                file.setFileId(Integer.valueOf(fileId));
                file.setFileName(name);
                file.setRemarks(remark);

                if (filePart != null && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                    // 删除旧文件
                    if (oldFile != null) {
                        Path oldFilePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "/static", oldFile.getFilePath());
                        try {
                            Files.delete(oldFilePath);
                            System.out.println("删除旧文件: " + oldFilePath);
                        } catch (IOException e) {
                            System.out.println("删除旧文件失败: " + oldFilePath);
                            e.printStackTrace();
                        }
                    }
                    file.setFilePath(insertDirectory + fileName);
                    file.setType(fileExtension);
                } else {
                    // 保留旧文件路径
                    file.setFilePath(oldFile != null ? oldFile.getFilePath() : null);
                    file.setType(oldFile.getType());
                }
            }

            // 添加或更新文件记录
            fileDao.addFile(file, userId);
            System.out.println("文件记录更新成功，文件ID: " + file.getFileId());

        } catch (Exception e) {
            System.out.println("文件上传过程中发生异常");
            e.printStackTrace();
        }
    }

    @Override
    public List<File> getFileByName(String keyword) {
        return fileDao.getFileByName(keyword);
    }
}
