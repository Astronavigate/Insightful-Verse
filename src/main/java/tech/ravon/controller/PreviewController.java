package tech.ravon.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PreviewController {

    @Value("${files.dir}")
    private String filesDir;

    @GetMapping("/preview")
    public String previewPage(@RequestParam String file, Map<String,Object> model) {
        model.put("fileName", file);
        return "/InsightfulVerse/CODEView"; // Thymeleaf 模板
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) throws IOException {
        Path f = Paths.get(filesDir).resolve(fileName);
        if (!Files.exists(f)) return ResponseEntity.notFound().build();
        Resource res = new FileSystemResource(f);

        MediaType type = MediaType.APPLICATION_OCTET_STREAM;
        String ext = fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase();
        switch(ext) {
            case "pdf": type = MediaType.APPLICATION_PDF; break;
            case "txt": type = MediaType.TEXT_PLAIN; break;
        }

        return ResponseEntity.ok()
                .contentType(type)
                .body(res);
    }
}
