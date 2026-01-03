package tech.ravon.service.inver.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.ravon.service.inver.CodeService;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CodeServiceImpl implements CodeService {

    private static final Logger logger = LoggerFactory.getLogger(CodeServiceImpl.class);

    // 统一保存路径
    private static final String BASE_TEMP_DIR = "src/main/resources/static/code/temp/";

    @Override
    public String runCode(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");
        String lang = request.getParameter("lang");

        if (!isCodeSafe(code, lang)) {
            return "Exception: Unsafe code detected.";
        }

        try {
            switch (lang.toLowerCase()) {
                case "java":
                    return runJava(code);
                case "python", "py":
                    return runPython(code);
                case "c":
                    return runC(code);
                case "cpp", "c++":
                    return runCpp(code);
                case "rust", "rs":
                    return runRust(code);
                default:
                    return "Not supported language.";
            }
        } catch (Exception e) {
            logger.error("Error running code", e);
            return "Exception: " + e.getMessage();
        }
    }

    // ------------------- Java -------------------
    @Override
    public String runJava(String code) {
        String workDir = createWorkDirectory("java");
        String fileName = "Main.java";
        String filePath = workDir + File.separator + fileName;

        try {
            Files.writeString(Paths.get(filePath), code);
            logger.info("[Java] Source saved at {}", filePath);

            // 编译
            ProcessBuilder compilePb = new ProcessBuilder("javac", fileName);
            compilePb.directory(new File(workDir));
            compilePb.redirectErrorStream(true);
            Process compile = compilePb.start();
            String compileOutput = readProcessOutput(compile);
            compile.waitFor();
            logger.info("[Java] WorkDir: {}", workDir);
            logger.info("[Java] Compile output:\n{}", compileOutput);
            if (compile.exitValue() != 0) {
                return "Compilation Error:\n" + compileOutput;
            }

            // 运行
            ProcessBuilder runPb = new ProcessBuilder("java", "Main");
            runPb.directory(new File(workDir));
            runPb.redirectErrorStream(true);
            Process run = runPb.start();
            String runOutput = readProcessOutputWithTimeout(run, 10);
            run.waitFor();
            logger.info("[Java] Run output:\n{}", runOutput);

            return runOutput;

        } catch (IOException | InterruptedException e) {
            logger.error("[Java] Exception", e);
            return "Exception: " + e.getMessage();
        } finally {
            try {
                deleteDirectory(Path.of(workDir + File.separator));
            } catch (Exception e) {
                logger.error("[File] Exception", e);
            }
        }
    }

    // ------------------- Python -------------------
    @Override
    public String runPython(String code) {
        String workDir = createWorkDirectory("python");
        String fileName = "main.py";
        String filePath = workDir + File.separator + fileName;

        try {
            Files.writeString(Paths.get(filePath), code);
            logger.info("[Python] Source saved at {}", filePath);

            ProcessBuilder pb = isCommandAvailable("python3") ?
                    new ProcessBuilder("python3", fileName) :
                    new ProcessBuilder("python", fileName);

            pb.directory(new File(workDir));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = readProcessOutputWithTimeout(process, 10);
            process.waitFor();

            logger.info("[Python] Run output:\n{}", output);
            return output;

        } catch (IOException | InterruptedException e) {
            logger.error("[Python] Exception", e);
            return "Exception: " + e.getMessage();
        } finally {
            try {
                deleteDirectory(Path.of(workDir + File.separator));
            } catch (Exception e) {
                logger.error("[File] Exception", e);
            }
        }
    }

    // ------------------- C -------------------
    @Override
    public String runC(String code) {
        return runCompiledCode(code, "c", "main.c", "c_exec.exe", "clang");
    }

    // ------------------- C++ -------------------
    @Override
    public String runCpp(String code) {
        return runCompiledCode(code, "cpp", "main.cpp", "cpp_exec.exe", "clang++", "-std=c++20", "-Wall");
    }

    // ------------------- Rust -------------------
    @Override
    public String runRust(String code) {
        return runCompiledCode(code, "rust", "main.rs", "rust_exec.exe", "rustc");
    }

    // ------------------- 通用编译方法 -------------------
    private String runCompiledCode(String code, String langFolder, String sourceFileName,
                                   String execFileName, String compiler, String... flags) {
        String workDir = createWorkDirectory(langFolder);
        Path sourcePath = Paths.get(workDir, sourceFileName);

        try {
            Files.writeString(sourcePath, code);
            logger.info("[{}] Source saved at {}", langFolder, sourcePath.toString());

            // 编译
            List<String> command = new ArrayList<>();
            command.add(compiler);
            for (String flag : flags) command.add(flag);
            command.add(sourceFileName);
            if (!compiler.equals("rustc")) { // Rust 输出用 -o
                command.add("-o");
                command.add(execFileName);
            } else {
                command.add("-o");
                command.add(execFileName);
            }

            logger.info("[{}] Compile command: {}", langFolder, command);
            ProcessBuilder compilePb = new ProcessBuilder(command);
            compilePb.directory(new File(workDir));
            compilePb.redirectErrorStream(true);
            Process compile = compilePb.start();
            String compileOutput = readProcessOutput(compile);
            compile.waitFor();
            logger.info("[{}] Compile output:\n{}", langFolder, compileOutput);
            if (compile.exitValue() != 0) {
                return "Compilation Error:\n" + compileOutput;
            }

            // 执行
            String execPath = workDir + File.separator + execFileName;
            ProcessBuilder runPb = new ProcessBuilder(execPath);
            runPb.directory(new File(workDir));
            runPb.redirectErrorStream(true);
            logger.info("[{}] Executing: {}", langFolder, execPath);
            Process run = runPb.start();
            String runOutput = readProcessOutputWithTimeout(run, 10);
            run.waitFor();
            logger.info("[{}] Run output:\n{}", langFolder, runOutput);

            return runOutput;

        } catch (IOException | InterruptedException e) {
            logger.error("[{}] Exception", langFolder, e);
            return "Exception: " + e.getMessage();
        } finally {
            try {
                deleteDirectory(Path.of(workDir + File.separator));
            } catch (Exception e) {
                logger.error("[File] Exception", e);
            }
        }
    }

    // ------------------- 工具方法 -------------------
    private String createWorkDirectory(String lang) {
        try {
            String dir = BASE_TEMP_DIR + lang + File.separator + UUID.randomUUID();
            Path path = Paths.get(dir);
            Files.createDirectories(path);
            logger.info("Created work directory: {}", dir);
            return dir;
        } catch (IOException e) {
            logger.error("Error creating work directory", e);
            return BASE_TEMP_DIR + "default";
        }
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) output.append(line).append("\n");
        }
        return output.toString();
    }

    private String readProcessOutputWithTimeout(Process process, int timeoutSeconds)
            throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        Thread readerThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) output.append(line).append("\n");
            } catch (IOException e) {
                logger.error("Error reading process output", e);
            }
        });
        readerThread.start();

        for (int i = 0; i < timeoutSeconds * 10; i++) {
            if (!process.isAlive()) break;
            Thread.sleep(100);
        }

        if (process.isAlive()) {
            process.destroy();
            output.append("\n[Process terminated due to timeout]");
        }

        readerThread.join(1000);
        return output.toString();
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private boolean isCommandAvailable(String cmd) {
        try {
            Process p = new ProcessBuilder(cmd, "--version").start();
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCodeSafe(String code, String lang) {
        String[] commonUnsafePatterns = {
                "Runtime.getRuntime", "ProcessBuilder", ".exec(", "System.exit",
                "File(", "Files.write", "new File", "Files.create", "FileOutputStream",
                "Process.start", "native"
        };

        String[] langPatterns = null;
        switch (lang.toLowerCase()) {
            case "java":
                langPatterns = new String[]{"java.io.", "java.nio.", "java.net.", "ClassLoader", "SecurityManager", "System.set"};
                break;
            case "python":
            case "py":
                langPatterns = new String[]{"import os", "import sys", "__import__", "eval(", "exec(", "open(", "subprocess", "import socket", "import shutil"};
                break;
            case "c":
                langPatterns = new String[]{"#include <stdlib.h>", "system(", "exec(", "fork(", "popen(", "fopen(", "FILE *", "socket(", "connect("};
                break;
            case "cpp":
                langPatterns = new String[]{"#include <stdlib.h>", "system(", "exec(", "fork(", "popen(", "fopen(", "FILE *", "socket(", "connect("};
                break;
            case "rust":
            case "rs":
                langPatterns = new String[]{"use std::process::Command", "use std::fs::File", "use std::net::", "unsafe", "std::process::Command", "std::fs::", "std::io::"};
                break;
        }

        for (String p : commonUnsafePatterns) if (code.contains(p)) return false;
        if (langPatterns != null) for (String p : langPatterns) if (code.contains(p)) return false;
        return true;
    }
}
