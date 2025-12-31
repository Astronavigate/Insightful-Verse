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
import tech.ravon.service.iviep.AnnotationService;
import tech.ravon.service.iviep.FileService;
import tech.ravon.service.iviep.ViewRecordService;

import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileDao fileDao;

    @Lazy
    @Autowired
    private ViewRecordService viewRecordService;

    @Lazy
    @Autowired
    private AnnotationService annotationService;

    /* ===================== upload limits ===================== */

    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 32;
    private static final int MAX_FILE_SIZE    = 1024 * 1024 * 2000;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 2047;

    /* ===================== helpers ===================== */

    private String resolveInsertDirectory(String ext) {
        if (ext == null) return "/media/";
        ext = ext.toLowerCase();

        if (ext.matches("doc|docx|xls|xlsx|ppt|pptx|csv|txt|md|pdf|epub"))
            return "/doc/";

        if (ext.matches("mp4|mkv|mov|wmv|wav|wma|mp3|flac|m4a"))
            return "/media/";

        if (ext.matches("jpg|jpeg|heif|raw|png|gif|webp|ico"))
            return "/img/";

        if (ext.matches("cpp|py|c|java|h|html|css|js|jsp|php|pyc|aspx|ts|rs|sql"))
            return "/code/";

        return "/etc/";
    }

    private Path resolveStaticPath(String insertDir) throws Exception {
        Path path = Paths.get(
                System.getProperty("user.dir"),
                "src", "main", "resources",
                "static" + insertDir
        );
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    private String extractBaseName(String path) {
        String name = Paths.get(path).getFileName().toString();
        return name.substring(0, name.lastIndexOf('.'));
    }

    /* ===================== service ===================== */

    @Override
    public String getFile(HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    @Override
    public File getFileById(Long fileId) {
        return fileDao.getFileById(fileId);
    }

    @Override
    public List<File> getCourseFiles(Long courseId) {
        return fileDao.getFilesByCourse(courseId);
    }

    @Override
    public List<File> getFileByName(String keyword) {
        return fileDao.getFileByName(keyword);
    }

    @Override
    public void deleteFile(HttpServletRequest request, Long fileId) {
        Long userId = ((User) request.getSession().getAttribute("user")).getUserId();
        File file = fileDao.getFileById(fileId);
        if (file == null) {
            throw new IllegalArgumentException("File not found: " + fileId);
        }

        try {
            Path filePath = Paths.get(
                    System.getProperty("user.dir"),
                    "src", "main", "resources",
                    "static",
                    file.getFilePath()
            );
            Files.deleteIfExists(filePath);
        } catch (Exception ignored) {}
        annotationService.deleteAnnotationsByUserAndBook(userId, fileId);
        viewRecordService.delRecordByFileId(fileId);
        fileDao.deleteFile(fileId);
    }

    @Override
    public void deleteCourseFiles(HttpServletRequest request, Long courseId) {
        List<File> files = fileDao.getFilesByCourse(courseId);
        for (File file : files) {
            deleteFile(request, file.getFileId());
        }
    }

    @Override
    public void updFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) return;

            Long userId     = user.getUserId();
            String fileId   = request.getParameter("fileId");
            String name     = request.getParameter("fileName");
            String remark   = request.getParameter("fileRemark");
            String courseId = request.getParameter("courseId");

            Part filePart     = request.getPart("file");
            Part subtitlePart = request.getPart("subtitleFile");

            boolean hasMedia =
                    filePart != null &&
                            filePart.getSubmittedFileName() != null &&
                            !filePart.getSubmittedFileName().isEmpty();

            boolean hasSubtitle =
                    subtitlePart != null &&
                            subtitlePart.getSubmittedFileName() != null &&
                            !subtitlePart.getSubmittedFileName().isEmpty();

            File oldFile = (fileId != null && !fileId.isEmpty())
                    ? fileDao.getFileById(Long.valueOf(fileId))
                    : null;

            String baseName;
            if (hasMedia) {
                baseName = Generators.timeBasedEpochGenerator().generate().toString();
            } else if (oldFile != null) {
                baseName = extractBaseName(oldFile.getFilePath());
            } else {
                return;
            }

            String mediaExt = null;
            String insertDir;
            Path staticPath;

            /* ---------- media ---------- */
            if (hasMedia) {
                String filename = filePart.getSubmittedFileName();
                mediaExt = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

                insertDir  = resolveInsertDirectory(mediaExt);
                staticPath = resolveStaticPath(insertDir);

                try (InputStream in = filePart.getInputStream()) {
                    Files.copy(
                            in,
                            staticPath.resolve(baseName + "." + mediaExt),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }
            } else {
                insertDir  = oldFile.getFilePath().substring(0, oldFile.getFilePath().lastIndexOf('/') + 1);
                staticPath = resolveStaticPath(insertDir);
            }

            /* ---------- subtitle ---------- */
            if (hasSubtitle) {
                String filename = subtitlePart.getSubmittedFileName();
                String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

                try (InputStream in = subtitlePart.getInputStream()) {
                    Files.copy(
                            in,
                            staticPath.resolve(baseName + "." + ext),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }

            /* ---------- cleanup ---------- */
            if (oldFile != null && hasMedia) {
                Path oldPath = Paths.get(
                        System.getProperty("user.dir"),
                        "src", "main", "resources",
                        "static",
                        oldFile.getFilePath()
                );
                Files.deleteIfExists(oldPath);
            }

            /* ---------- database ---------- */
            File file = new File();
            file.setCourseId(Long.valueOf(courseId));
            file.setFileName(name);
            file.setRemarks(remark);

            if (fileId == null || fileId.isEmpty()) {
                file.setType(mediaExt);
                file.setFilePath(insertDir + baseName + "." + mediaExt);
            } else {
                file.setFileId(Long.valueOf(fileId));
                if (hasMedia) {
                    file.setType(mediaExt);
                    file.setFilePath(insertDir + baseName + "." + mediaExt);
                } else {
                    file.setType(oldFile.getType());
                    file.setFilePath(oldFile.getFilePath());
                }
            }

            fileDao.updFile(file, userId);

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
