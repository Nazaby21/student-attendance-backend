package com.example.demo.controller;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<UserResponse> addStudent(@RequestBody UserRequest studentRequest, @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(studentService.addStudent(studentRequest, classId));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateStudent(@PathVariable Long id, @RequestBody UserRequest studentRequest, @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentRequest, classId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
