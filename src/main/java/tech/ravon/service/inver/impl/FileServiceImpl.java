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

import com.fasterxml.uuid.Generators;
import io.documentnode.epub4j.domain.Book;
import io.documentnode.epub4j.epub.EpubReader;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import tech.ravon.mapper.FileDao;
import tech.ravon.model.inver.File;
import tech.ravon.model.inver.User;
import tech.ravon.service.VectorService;
import tech.ravon.service.inver.AnnotationService;
import tech.ravon.service.inver.FileService;
import tech.ravon.service.inver.ViewRecordService;
import tech.ravon.vo.inver.FileVO;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final FileDao fileDao;
    private final ViewRecordService viewRecordService;
    private final AnnotationService annotationService;
    private final VectorService vectorService;

    public FileServiceImpl(
            FileDao fileDao,
            @Lazy ViewRecordService viewRecordService,
            @Lazy AnnotationService annotationService,
            @Lazy VectorService vectorService) {
        this.fileDao = fileDao;
        this.viewRecordService = viewRecordService;
        this.annotationService = annotationService;
        this.vectorService = vectorService;
    }

    /* ===================== upload limits ===================== */

    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 32;
    private static final int MAX_FILE_SIZE    = 1024 * 1024 * 2000;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 2047;

    /* ===================== helpers ===================== */

    /**
     * 返回相对目录名（不带前导或尾部斜杠），例如 "doc", "media", "img"
     */
    private String resolveInsertDirectory(String ext) {
        if (ext == null) return "/etc";
        ext = ext.toLowerCase();

        if (ext.matches("doc|docx|xls|xlsx|ppt|pptx|csv|txt|md|pdf|epub"))
            return "/doc";

        if (ext.matches("mp4|mkv|mov|wmv|wav|wma|mp3|flac|m4a"))
            return "/media";

        if (ext.matches("jpg|jpeg|heif|raw|png|gif|webp|ico"))
            return "/img";

        if (ext.matches("cpp|py|c|java|h|html|css|js|jsp|php|pyc|aspx|ts|rs|sql"))
            return "/code";

        return "/etc";
    }

    /**
     * 将 insertDir（相对目录名）解析到 ${user.dir}/data/<insertDir>
     */
    private Path resolveStaticPath(String insertDir) throws Exception {
        Path path = Paths.get(
                System.getProperty("user.dir"),
                "data",
                insertDir
        );
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    private String extractBaseName(String path) {
        String name = Paths.get(path).getFileName().toString();
        int idx = name.lastIndexOf('.');
        return idx == -1 ? name : name.substring(0, idx);
    }

    /* ===================== service ===================== */

    @Override
    public List<File> listFiles() {
        return fileDao.getAllFiles();
    }

    @Override
    public List<File> getFilesByPop(Long limit) {
        if (limit == 0L) {
            limit = 10L;
        }
        return fileDao.getFilesByPop(limit);
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
    public List<FileVO> getCourseFilesVO(Long userId, Long courseId) {
        return fileDao.getFilesVOByCourse(userId, courseId);
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
                    "data",
                    file.getFilePath()
            );
            Files.deleteIfExists(filePath);
        } catch (Exception ignored) {}

        // 删除注释与浏览记录、数据库记录
        annotationService.deleteAnnotationsByUserAndBook(userId, fileId);
        viewRecordService.delRecordByFileId(fileId);
        fileDao.deleteFile(fileId);
        vectorService.delVector(String.valueOf(fileId), "inver.files");
    }

    @Override
    public void deleteCourseFiles(HttpServletRequest request, Long courseId) {
        List<File> files = fileDao.getFilesByCourse(courseId);
        for (File file : files) {
            deleteFile(request, file.getFileId());
        }
    }

    @Override
    public File updFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user == null) return null;

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
                return null;
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
                String oldPath = oldFile.getFilePath();
                int lastSlash = oldPath.lastIndexOf('/');
                insertDir = (lastSlash == -1) ? "" : oldPath.substring(0, lastSlash);
                staticPath = resolveStaticPath(insertDir);
            }

            /* ---------- subtitle ---------- */
            boolean shouldTranscribe = false;
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
            } else if (hasMedia) {
                // 检查内嵌字幕
                Path mediaPath = staticPath.resolve(baseName + "." + mediaExt);
                shouldTranscribe = !hasEmbeddedSubtitle(mediaPath.toString());
            }

            /* ---------- cleanup old files ---------- */
            if (oldFile != null && hasMedia) {
                Path oldPath = Paths.get(
                        System.getProperty("user.dir"),
                        "data",
                        oldFile.getFilePath()
                );

                Path parentDir = oldPath.getParent();
                String fileName = oldPath.getFileName().toString();
                int dotIndex = fileName.lastIndexOf('.');
                String prefix = ((dotIndex == -1) ? fileName : fileName.substring(0, dotIndex)) + ".";

                if (parentDir != null && Files.exists(parentDir)) {
                    try (var stream = Files.list(parentDir)) {
                        stream.filter(path -> path.getFileName().toString().startsWith(prefix))
                                .forEach(path -> {
                                    try { Files.deleteIfExists(path); }
                                    catch (IOException ignored) {}
                                });
                    }
                }
            }

            /* ---------- database ---------- */
            File file = new File();
            file.setCourseId(Long.valueOf(courseId));
            file.setFileName(name);
            file.setRemarks(remark);

            if (fileId == null || fileId.isEmpty()) {
                file.setType(mediaExt);
                file.setFilePath(insertDir + "/" + baseName + "." + mediaExt);
            } else {
                file.setFileId(Long.valueOf(fileId));
                if (hasMedia) {
                    file.setType(mediaExt);
                    file.setFilePath(insertDir + "/" + baseName + "." + mediaExt);
                } else {
                    file.setType(oldFile.getType());
                    file.setFilePath(oldFile.getFilePath());
                }
            }

            fileDao.updFile(file, userId);

            createThumbnail(file.getFilePath());

            File newFile = fileDao.getFileByInfo(file);

            String fileHref = "/InsightfulVerse/File?fileId=" + newFile.getFileId();
            String contentForVector = "<a href=\"" + fileHref +"\">" + "FileName: " + name + "\nRemark: " + remark + "</a>";

            vectorService.updVector(String.valueOf(file.getFileId()), "inver.files", contentForVector);

            /* ---------- 异步转写 SRT ---------- */
            if (shouldTranscribe) {
                String fullPath = Paths.get(System.getProperty("user.dir"), "data", file.getFilePath()).toString();
                new Thread(() -> {
                    try {
                        URL url = new URL("http://127.0.0.1:7824/transcribe");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        String json = "{\"audio_path\": \"" + fullPath.replace("\\", "\\\\") + "\"}";
                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(json.getBytes(StandardCharsets.UTF_8));
                        }

                        int code = conn.getResponseCode();
                        if (code != 200) {
                            System.err.println("Transcribe request failed: " + code);
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            return newFile;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    /* ---------- 辅助方法 ---------- */

    /**
     * 检查文件是否包含内嵌字幕
     */
    private boolean hasEmbeddedSubtitle(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error",
                    "-select_streams", "s",
                    "-show_entries", "stream=index",
                    "-of", "csv=p=0",
                    filePath
            );
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().findAny().isPresent();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final int MAX_EDGE = 1920;
    private static final String SUFFIX = "-Thumbnail.jpg";

    public String createThumbnail(String filePath) {
        if (filePath == null || filePath.isEmpty()) return null;

        try {
            java.io.File sourceFile = Paths.get(System.getProperty("user.dir"), "data", filePath).toFile();
            if (!sourceFile.exists()) return null;

            String nameNoExt = sourceFile.getName();
            int dot = nameNoExt.lastIndexOf('.');
            if (dot != -1) nameNoExt = nameNoExt.substring(0, dot);

            java.io.File thumbFile = new java.io.File(sourceFile.getParentFile(), nameNoExt + SUFFIX);
            if (thumbFile.exists()) return thumbFile.getAbsolutePath();

            BufferedImage raw = extractRawImage(sourceFile);
            if (raw == null) return null;

            BufferedImage scaled = scaleImage(raw);
            ImageIO.write(scaled, "jpg", thumbFile);

            return thumbFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("缩略图生成中断 [{}]: {}", filePath, e.getMessage());
            return null;
        }
    }

    private BufferedImage extractRawImage(java.io.File file) {
        String name = file.getName().toLowerCase();
        String ext = getExt(name);

        try {
            // 1. 媒体类：视频与音频 (解决 FLAC 卡顿)
            if (ext.matches("mp4|mkv|mov|wmv|avi|webm|mp3|flac|m4a|wav|wma")) {
                return extractMediaFrame(file);
            }

            // 2. 电子书 (使用 epub4j-core)
            if (ext.equals("epub")) {
                return extractEpubCover(file);
            }

            // 3. 文档类
            if (ext.equals("pdf")) {
                try (PDDocument doc = Loader.loadPDF(file)) {
                    return new PDFRenderer(doc).renderImage(0, 1.5f);
                }
            }
            if (ext.matches("ppt|pptx")) {
                return renderPptFirstSlide(file);
            }
            if (ext.matches("xls|xlsx|csv")) {
                try (InputStream is = new FileInputStream(file);
                     Workbook wb = WorkbookFactory.create(is)) {
                    return renderExcel(wb.getSheetAt(0));
                }
            }

            // 4. 文本/代码类 (防御性渲染)
            if (ext.matches("doc|docx|txt|md|java|py|cpp|c|h|html|css|js|ts|rs|sql|php|jsp|aspx")) {
                return renderTextOverlay(file, ext.toUpperCase());
            }

            // 5. 图片类
            if (ext.matches("jpg|jpeg|png|gif|webp|ico|raw|heif")) {
                return ImageIO.read(file);
            }

        } catch (Throwable t) {
            log.warn("解析格式 [{}] 失败，使用占位图替代", ext);
        }

        return renderDefaultPlaceholder(ext.toUpperCase());
    }

    private BufferedImage extractMediaFrame(java.io.File file) {
        // 孤鹰原则：不猜测、不等待、不妥协
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file)) {
            // 1. 扩容探测：WebP 封面往往在文件头部需要更多数据才能定界
            grabber.setOption("probesize", "5242880");      // 5MB 探测空间
            grabber.setOption("analyzeduration", "2000000"); // 2秒探测时间

            // 2. 卸载负重：强制不加载音频流，专注于封面提取
            grabber.setAudioStream(-1);

            grabber.start();

            // 3. 动态像素转换：不再强制 bgr24，让 Java2DFrameConverter 处理格式转换
            Frame frame = null;

            // WebP 封面可能需要连续 grab 才能跳过元数据包
            for (int i = 0; i < 15; i++) {
                frame = grabber.grabImage();
                // 只要抓取到图像数据且宽度大于 0，立即撤退
                if (frame != null && frame.image != null && frame.imageWidth > 0) {
                    break;
                }
            }

            if (frame == null || frame.image == null) {
                log.warn("未能从媒体文件 [{}] 中剥离有效图像流", file.getName());
                grabber.stop();
                return renderDefaultPlaceholder("WEBP/IMG");
            }

            BufferedImage bi = new Java2DFrameConverter().getBufferedImage(frame);
            grabber.stop();
            return bi;
        } catch (Throwable e) {
            log.error("媒体解析核心崩溃 [{}]: {}", file.getName(), e.getMessage());
            return renderDefaultPlaceholder("MEDIA");
        }
    }

    private BufferedImage extractEpubCover(java.io.File file) {
        try (InputStream is = new FileInputStream(file)) {
            Book book = new EpubReader().readEpub(is);

            // 1. 尝试获取官方定义的封面
            if (book.getCoverImage() != null) {
                return ImageIO.read(new ByteArrayInputStream(book.getCoverImage().getData()));
            }

            // 2. 容错逻辑：寻找资源库中第一个出现的图片文件
            // 很多电子书虽然没标记封面，但第一个图片通常就是封面
            for (io.documentnode.epub4j.domain.Resource resource : book.getResources().getAll()) {
                String href = resource.getHref().toLowerCase();
                if (href.endsWith(".jpg") || href.endsWith(".jpeg") || href.endsWith(".png")) {
                    log.info("从资源路径探测到疑似封面: {}", resource.getHref());
                    return ImageIO.read(new ByteArrayInputStream(resource.getData()));
                }
            }
        } catch (Exception e) {
            log.warn("EPUB 解析中断 [{}]: {}", file.getName(), e.getMessage());
        }

        // 3. 实在找不到，返回冷峻风格的占位图
        return renderDefaultPlaceholder("EPUB");
    }

    private BufferedImage renderPptFirstSlide(java.io.File file) throws Exception {
        try (FileInputStream is = new FileInputStream(file);
             XMLSlideShow ppt = new XMLSlideShow(is)) {
            if (ppt.getSlides().isEmpty()) return null;
            Dimension pg = ppt.getPageSize();
            BufferedImage img = new BufferedImage(pg.width, pg.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, pg.width, pg.height);
            ppt.getSlides().get(0).draw(g);
            g.dispose();
            return img;
        }
    }

    private BufferedImage renderTextOverlay(java.io.File file, String label) {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), decoder))) {
            List<String> lines = reader.lines().limit(40).collect(Collectors.toList());
            BufferedImage bi = new BufferedImage(1200, 1600, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bi.createGraphics();
            g.setColor(new Color(15, 17, 26));
            g.fillRect(0, 0, 1200, 1600);
            g.setColor(new Color(100, 120, 140));
            g.setFont(new Font("SansSerif", Font.BOLD, 60));
            g.drawString(label, 50, 100);
            g.setColor(new Color(173, 186, 199));
            g.setFont(new Font("Consolas", Font.PLAIN, 20));
            int y = 180;
            for (String line : lines) {
                String clean = line.replace("\t", "    ");
                g.drawString(clean.length() > 55 ? clean.substring(0, 55) : clean, 50, y);
                y += 35;
            }
            g.dispose();
            return bi;
        } catch (Exception e) {
            return renderDefaultPlaceholder(label);
        }
    }

    private BufferedImage renderDefaultPlaceholder(String ext) {
        BufferedImage bi = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(20, 22, 30));
        g.fillRect(0, 0, 800, 800);
        g.setColor(new Color(45, 50, 65));
        g.setStroke(new BasicStroke(4));
        g.drawRect(40, 40, 720, 720);
        g.setColor(new Color(173, 186, 199));
        g.setFont(new Font("Consolas", Font.BOLD, 100));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(ext, (800 - fm.stringWidth(ext)) / 2, 420);
        g.dispose();
        return bi;
    }

    private BufferedImage scaleImage(BufferedImage src) {
        int size = 1000;
        int w = src.getWidth();
        int h = src.getHeight();

        // 1. 创建目标正方形画布
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();

        // 开启高质量渲染
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ---------------------------------------------------------
        // 步骤 A：绘制模糊背景 (Blur Background)
        // ---------------------------------------------------------
        // 将原图强制拉伸填充整个 1000x1000 区域
        g.drawImage(src, 0, 0, size, size, null);

        // 覆盖一层半透明深色蒙版，增加冷峻感与深邃感 (符合你对夜色的偏好)
        g.setColor(new Color(10, 12, 18, 180)); // 深色且 70% 不透明度
        g.fillRect(0, 0, size, size);

        // ---------------------------------------------------------
        // 步骤 B：绘制等比例原图 (Center Contain)
        // ---------------------------------------------------------
        // 计算缩放因子，确保原图完整显示且不拉伸 (Contain 模式)
        double scale = Math.min((double) size / w, (double) size / h);
        int sw = (int) (w * scale);
        int sh = (int) (h * scale);

        // 居中位置计算
        int x = (size - sw) / 2;
        int y = (size - sh) / 2;

        // 绘制顶层清晰原图
        g.drawImage(src, x, y, sw, sh, null);

        g.dispose();
        return out;
    }

    private BufferedImage renderExcel(Sheet sheet) {
        BufferedImage img = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(10, 15, 25));
        g.fillRect(0, 0, 1200, 800);
        g.setColor(new Color(173, 186, 199));
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        for (int i = 0; i < 20; i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;
            for (int j = 0; j < 6; j++) {
                Cell c = r.getCell(j);
                String val = (c == null) ? "" : c.toString();
                g.drawString(val.length() > 15 ? val.substring(0, 12) + ".." : val, 30 + j * 180, 50 + i * 35);
            }
        }
        g.dispose();
        return img;
    }

    private String getExt(String name) {
        int dot = name.lastIndexOf('.');
        return (dot == -1) ? "" : name.substring(dot + 1).toLowerCase();
    }
}
