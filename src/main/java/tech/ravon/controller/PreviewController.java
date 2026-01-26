package tech.ravon.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class PreviewController {

    @Value("${files.dir}")
    private String filesDir;

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Path f = Paths.get(filesDir).resolve(fileName);
        if (!Files.exists(f)) return ResponseEntity.notFound().build();
        Resource res = new FileSystemResource(f);

        MediaType type = MediaType.APPLICATION_OCTET_STREAM;
        String ext = fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase();
        type = switch (ext) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt" -> MediaType.TEXT_PLAIN;
            default -> type;
        };

        return ResponseEntity.ok()
                .contentType(type)
                .body(res);
    }
}
