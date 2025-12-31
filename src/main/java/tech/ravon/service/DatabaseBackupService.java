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

package tech.ravon.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseBackupService {

    // 备份目录（已包含 user.dir 路径）
    private static final String BACKUP_DIR = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "dumps").toString();

    // 可执行文件所在的目录（如果你把 pg_dump 放在项目下的 static/bin，调整此处）
    private static final String FOLDER = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "bin", "postgres18").toString();

    // 数据库配置信息（按需修改）
    private static final String DB_NAME = "ravon";           // 数据库名
    private static final String DB_USER = "ravon";           // 数据库用户名
    private static final String DB_PASSWORD = "11725110307"; // 密码（示例：与原来一致）
    private static final String SCHEMA = "inver";            // 要导出的 schema（如果要导出全部 schema，设置为 null 或空字符串）

    private static final String FILE_PREFIX = "ravon_backup_";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String DUMP_PATH;

    @PostConstruct
    public void init() {
        System.out.println("Backup dir: " + BACKUP_DIR);
        detectOsAndSetDumpCommand();
        backupDatabase();  // 启动时立即备份
    }

    private void detectOsAndSetDumpCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            DUMP_PATH = Paths.get(FOLDER, "pg_dump.exe").toString();
            System.out.println("Detected OS: Windows\nUse " + DUMP_PATH);
        } else {
            DUMP_PATH = Paths.get(FOLDER, "pg_dump").toString();
            System.out.println("Detected OS: Unix-like\nUse " + DUMP_PATH);
        }
        // 如果你更希望直接使用系统 PATH 下的 pg_dump，把上面 DUMP_PATH 改成 "pg_dump"
    }

    // 每7天备份一次数据库
    @Scheduled(cron = "0 0 0 */7 * *")  // 每7天凌晨0点执行
    public void backupDatabase() {
        // 当前日期
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DATE_FORMATTER);

        String backupFileName = FILE_PREFIX + formattedDate + ".sql";
        String backupFilePath = Paths.get(BACKUP_DIR, backupFileName).toString();

        // 确保备份目录存在
        File backupDirFile = new File(BACKUP_DIR);
        if (!backupDirFile.exists()) {
            boolean ok = backupDirFile.mkdirs();
            if (!ok) {
                System.err.println("Failed to create backup directory: " + BACKUP_DIR);
                // 仍然尝试继续（可能后续会报错）
            }
        }

        // 构建 pg_dump 命令
        // 导出特定 schema 使用: -n schema_name
        // 输出为 plain SQL（默认），并通过 -f 指定文件路径
        List<String> cmd = new ArrayList<>();
        cmd.add(DUMP_PATH);
        cmd.add("-U");
        cmd.add(DB_USER);
        if (SCHEMA != null && !SCHEMA.isBlank()) {
            cmd.add("-n");
            cmd.add(SCHEMA);
        }
        cmd.add("-f");
        cmd.add(backupFilePath);
        cmd.add(DB_NAME);

        ProcessBuilder pb = new ProcessBuilder(cmd);

        // 使用环境变量 PGPASSWORD 传递密码（注意安全性）
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", DB_PASSWORD);

        // 可选：设置工作目录
        pb.directory(new File(System.getProperty("user.dir")));

        try {
            System.out.println("Executing pg_dump: " + String.join(" ", cmd));
            Process p = pb.start();
            int exitCode = p.waitFor();
            System.out.println("pg_dump exit code: " + exitCode);
            if (exitCode == 0) {
                System.out.println("Database backup successful: " + backupFilePath);
            } else {
                System.err.println("Database backup failed. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 每60天清理一次备份，保留60天内每30天的第一份
    @Scheduled(cron = "0 0 0 1 */2 *")  // 每2个月的第一天执行
    public void cleanOldBackups60Days() {
        try {
            LocalDate now = LocalDate.now();
            Path backupPath = Paths.get(BACKUP_DIR);

            if (!Files.exists(backupPath) || !Files.isDirectory(backupPath)) {
                System.out.println("Backup path does not exist or is not a directory: " + backupPath);
                return;
            }

            final List<Path> backupsToKeep = new ArrayList<>();

            Files.list(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();
                        if (fileName.startsWith(FILE_PREFIX) && fileName.endsWith(".sql")) {
                            // 提取中间的日期部分
                            String dateStr = fileName.substring(FILE_PREFIX.length(), fileName.length() - 4);
                            try {
                                LocalDate backupDate = LocalDate.parse(dateStr, DATE_FORMATTER);
                                if (!backupDate.isBefore(now.minusDays(60))) {
                                    // 保留每30天的第一份：保留那些 dayOfMonth==1 或 (dayOfMonth-1) % 30 == 0
                                    int dom = backupDate.getDayOfMonth();
                                    if (dom == 1 || ((dom - 1) % 30 == 0)) {
                                        backupsToKeep.add(file);
                                    }
                                }
                            } catch (Exception ex) {
                                // 忽略无法解析的文件名
                            }
                        }
                    });

            // 删除不需要的备份
            Files.list(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        if (!backupsToKeep.contains(file) && file.getFileName().toString().startsWith(FILE_PREFIX)) {
                            try {
                                Files.delete(file);
                                System.out.println("Deleted expired backup file: " + file);
                            } catch (IOException e) {
                                System.err.println("Failed to delete backup file: " + file);
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 每360天清理一次备份，保留360天内每90天的第一份
    @Scheduled(cron = "0 0 0 1 1,4,7,10 *")  // 每年第一天和季度第一天执行
    public void cleanOldBackups360Days() {
        try {
            LocalDate now = LocalDate.now();
            Path backupPath = Paths.get(BACKUP_DIR);

            if (!Files.exists(backupPath) || !Files.isDirectory(backupPath)) {
                System.out.println("Backup path does not exist or is not a directory: " + backupPath);
                return;
            }

            final List<Path> backupsToKeep = new ArrayList<>();

            Files.list(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();
                        if (fileName.startsWith(FILE_PREFIX) && fileName.endsWith(".sql")) {
                            String dateStr = fileName.substring(FILE_PREFIX.length(), fileName.length() - 4);
                            try {
                                LocalDate backupDate = LocalDate.parse(dateStr, DATE_FORMATTER);
                                if (!backupDate.isBefore(now.minusDays(360))) {
                                    int dayOfYear = backupDate.getDayOfYear();
                                    // 保留每90天的第一份：dayOfYear==1 或 (dayOfYear-1) % 90 == 0
                                    if (dayOfYear == 1 || ((dayOfYear - 1) % 90 == 0)) {
                                        backupsToKeep.add(file);
                                    }
                                }
                            } catch (Exception ex) {
                                // 忽略无法解析的文件名
                            }
                        }
                    });

            Files.list(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        if (!backupsToKeep.contains(file) && file.getFileName().toString().startsWith(FILE_PREFIX)) {
                            try {
                                Files.delete(file);
                                System.out.println("Deleted expired backup file: " + file);
                            } catch (IOException e) {
                                System.err.println("Failed to delete backup file: " + file);
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
