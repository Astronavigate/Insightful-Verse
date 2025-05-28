package tech.eagloxis.service.iviep.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.eagloxis.service.iviep.CodeService;

import java.io.*;

@Service
public class CodeServiceImpl implements CodeService {

    private static final Logger logger = LoggerFactory.getLogger(CodeServiceImpl.class);

    @Override
    public String runJava(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");
        String baseDir = System.getProperty("user.dir") + "/src/main/resources/test"; // Java源代码保存位置
        String filePath = baseDir + "/Main.java";
        String classPath = baseDir;  // 确保 classPath 指向 test 目录

        // 校验代码是否安全
        if (!isCodeSafe(code)) {
            response.setContentType("text/html;charset=UTF-8");
            return "Exception: Online compilation prohibits running another program or writing to local files.";
        }

        // 写入代码到文件
        File javaFile = new File(filePath);
        try (PrintStream stream = new PrintStream(new FileOutputStream(javaFile))) {
            stream.print("package test;\n\n" + code);
        } catch (IOException e) {
            logger.error("Error writing code to file", e);
            return "Exception: Error writing code to file.";
        }

        try {
            // 编译 Java 文件并将 class 输出到 test 目录
            ProcessBuilder compiler = new ProcessBuilder("javac", "-d", baseDir, filePath);
            compiler.redirectErrorStream(true);
            Process compilationProcess = compiler.start();
            compilationProcess.waitFor();
            if (compilationProcess.exitValue() != 0) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(compilationProcess.getInputStream()))) {
                    StringBuilder errorMsg = new StringBuilder();
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorMsg.append(line).append("\n");
                    }
                    return "Failed to compile Java file. Error message:<br>" + errorMsg.toString().replaceAll("\n", "<br>");
                }
            }

            // 执行 Java 文件
            String className = "test.Main"; // 执行的类
            ProcessBuilder executor = new ProcessBuilder("java", "-cp", classPath, className);
            executor.redirectErrorStream(true);
            Process executionProcess = executor.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(executionProcess.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                return output.toString().replaceAll("\n", "<br>");
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error running Java code", e);
            return "Exception: " + e.getMessage();
        } finally {
            // 清理临时文件
            if (javaFile.exists() && !javaFile.delete()) {
                logger.warn("Failed to delete temporary file: " + filePath);
            }
        }
    }

    /**
     * 校验代码是否安全
     */
    private boolean isCodeSafe(String code) {
        // 定义潜在危险的代码模式
        String[] unsafePatterns = {
                ".start()",   // 启动外部进程
                "process.",   // 操作进程
                "java.io",    // 文件 I/O 操作
                "Runtime.getRuntime",  // 执行系统命令
                "System."     // 系统级操作，可能修改环境变量等
        };

        // 如果包含 System.out 的代码，则不认为其不安全
        if (code.contains("System.out")) {
            code = code.replace("System.out", ""); // 移除掉 System.out 以继续安全检查
        }

        // 遍历所有不安全模式
        for (String pattern : unsafePatterns) {
            if (code.contains(pattern)) {
                // 输出详细日志，帮助调试
                System.out.println("Unsafe code pattern detected: " + pattern);
                return false;
            }
        }

        // 如果没有匹配到不安全模式，则认为代码是安全的
        return true;
    }
}
