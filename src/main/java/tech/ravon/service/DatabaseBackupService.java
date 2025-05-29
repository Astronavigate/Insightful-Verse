package tech.ravon.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseBackupService {

    private static final String BACKUP_DIR = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "dumps").toString();
    private static String FOLDER = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "bin").toString();
    private String DUMP_PATH;

    @PostConstruct
    public void init() {
        System.out.println(BACKUP_DIR);
        detectOsAndSetDumpCommand();
        backupDatabase();  // 启动时立即备份
    }

    private void detectOsAndSetDumpCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            DUMP_PATH = Paths.get(FOLDER, "mysqldump.exe").toString();
            System.out.println("Detected OS: Windows");
        } else {
            DUMP_PATH = Paths.get(FOLDER, "mysqldump").toString();
            System.out.println("Detected OS: Unix-like");
        }
    }

    // 每7天备份一次数据库
    @Scheduled(cron = "0 0 0 */7 * *")  // 每7天凌晨0点执行
    public void backupDatabase() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 定义格式化模式 "yyMMdd"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");

        // 格式化当前日期
        String formattedDate = currentDate.format(formatter);

        // 使用 Paths.get 拼接路径，保证跨平台
        String BACKUP_FILE = Paths.get(BACKUP_DIR, "iviep_backup_" + formattedDate + ".sql").toString();

        // 确保备份目录存在
        File backupDirFile = new File(BACKUP_DIR);
        if (!backupDirFile.exists()) {
            backupDirFile.mkdirs();
        }

        // mysqldump 命令及参数
        String[] command = {
                DUMP_PATH,
                "-u", "root",
                "-p11725110307",
                "iviep"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 重定向输出到指定文件
        processBuilder.redirectOutput(new File(BACKUP_FILE));

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
            if (exitCode == 0) {
                System.out.println("The database backup is successful! Backup file: " + BACKUP_FILE);
            } else {
                System.err.println("Database backup failed!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 每60天清理一次备份，保留60天内每30天的第一份
    @Scheduled(cron = "0 0 0 1 */2 *")  // 每2个月的第一天执行
    public void cleanOldBackups60Days() {
        try {
            // 获取当前日期
            LocalDate now = LocalDate.now();
            Path backupPath = Paths.get(System.getProperty("user.dir"), BACKUP_DIR);

            List<Path> backupsToKeep = new ArrayList<>();

            // 遍历目录中的所有备份文件
            Files.walk(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        // 获取文件的备份日期
                        String fileName = file.getFileName().toString();
                        if (fileName.startsWith("iviep_backup_")) {
                            String dateStr = fileName.substring(15, 23); // 获取备份日期部分
                            LocalDate backupDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

                            // 如果备份文件在60天之内，则按30天的间隔保留第一份备份
                            if (now.minusDays(60).isBefore(backupDate)) {
                                if (backupDate.getDayOfMonth() == 1 || backupDate.getDayOfMonth() % 30 == 1) {
                                    backupsToKeep.add(file); // 保留每30天的第一份
                                }
                            }
                        }
                    });

            // 删除不需要的备份文件
            Files.walk(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        if (!backupsToKeep.contains(file)) {
                            try {
                                Files.delete(file);
                                System.out.println("To delete an expired backup file: " + file);
                            } catch (IOException e) {
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
            // 获取当前日期
            LocalDate now = LocalDate.now();
            Path backupPath = Paths.get(System.getProperty("user.dir"), BACKUP_DIR);

            List<Path> backupsToKeep = new ArrayList<>();

            // 遍历目录中的所有备份文件
            Files.walk(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        // 获取文件的备份日期
                        String fileName = file.getFileName().toString();
                        if (fileName.startsWith("iviep_backup_")) {
                            String dateStr = fileName.substring(15, 23); // 获取备份日期部分
                            LocalDate backupDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

                            // 如果备份文件在360天之内，则按90天的间隔保留第一份备份
                            if (now.minusDays(360).isBefore(backupDate)) {
                                if (backupDate.getDayOfYear() == 1 || backupDate.getDayOfYear() % 90 == 1) {
                                    backupsToKeep.add(file); // 保留每90天的第一份
                                }
                            }
                        }
                    });

            // 删除不需要的备份文件
            Files.walk(backupPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        if (!backupsToKeep.contains(file)) {
                            try {
                                Files.delete(file);
                                System.out.println("To delete an expired backup file: " + file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
