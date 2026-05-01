package com.example.demo.controller;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassEntityController {

    private final ClassEntityService classService;

    @PostMapping
    public ResponseEntity<ClassEntityResponse> createClass(@RequestBody ClassEntityRequest classRequest) {
        return ResponseEntity.ok(classService.createClass(classRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassEntityResponse> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClassEntityResponse>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassEntityResponse> updateClass(@PathVariable Long id, @RequestBody ClassEntityRequest classRequest) {
        return ResponseEntity.ok(classService.updateClass(id, classRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}
