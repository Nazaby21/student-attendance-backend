package com.example.demo.controller;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassEntityController {

    private final ClassEntityService classService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassEntityResponse> createClass(@RequestBody ClassEntityRequest classRequest) {
        return ResponseEntity.ok(classService.createClass(classRequest));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ClassEntityResponse> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<ClassEntityResponse>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassEntityResponse> updateClass(@PathVariable Long id, @RequestBody ClassEntityRequest classRequest) {
        return ResponseEntity.ok(classService.updateClass(id, classRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
