package tech.ravon.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import tech.ravon.model.inver.Annotation;
import tech.ravon.service.inver.AnnotationService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class InsightfulVerseRestController {

    @Autowired
    private AnnotationService annotationService;

    /**
     * List annotations for current session user and a book.
     */
    @GetMapping("/annotation/list")
    public ResponseEntity<?> list(@RequestParam("bookId") Long bookId, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            // Return 401 or empty list depending on preference
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        // Replace this cast with your actual User class
        tech.ravon.model.inver.User user = (tech.ravon.model.inver.User) userObj;
        Long userId = user.getUserId();

        List<Annotation> list = annotationService.getAnnotations(userId, bookId);
        return ResponseEntity.ok(list);
    }

    /**
     * Save annotation. Session user required.
     * Request body is JSON of Annotation (id optional for update).
     */
    @PostMapping("/annotation/save")
    public ResponseEntity<?> save(@RequestBody Annotation annotation, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "not_logged_in"));
        }

        tech.ravon.model.inver.User user = (tech.ravon.model.inver.User) userObj;
        Long userId = user.getUserId();

        // ensure userId set on incoming annotation
        annotation.setUserId(userId);

        // Persist (Service handles insert/update)
        try {
            annotationService.saveAnnotation(annotation);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            // log in real project
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "save_failed", "message", e.getMessage()));
        }
    }

    /**
     * Delete annotation by id (session user required)
     */
    @PostMapping("/annotation/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","not_logged_in"));
        tech.ravon.model.inver.User user = (tech.ravon.model.inver.User) userObj;
        Long userId = user.getUserId();
        try {
            annotationService.deleteAnnotation(id, userId);
            return ResponseEntity.ok(Map.of("status","ok"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","delete_failed"));
        }
    }
}
