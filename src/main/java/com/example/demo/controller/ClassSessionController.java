package com.example.demo.controller;

import com.example.demo.dto.Request.ClassSessionRequest;
import com.example.demo.dto.Response.ClassSessionResponse;
import com.example.demo.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ClassSessionController {

    private final ClassSessionService sessionService;

    @PostMapping
    public ResponseEntity<ClassSessionResponse> createSession(@RequestBody ClassSessionRequest sessionRequest) {
        return ResponseEntity.ok(sessionService.createSession(sessionRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassSessionResponse> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClassSessionResponse>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<ClassSessionResponse>> getSessionsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(sessionService.getSessionsByClass(classId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
