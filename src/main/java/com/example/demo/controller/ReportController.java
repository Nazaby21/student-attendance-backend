package com.example.demo.controller;

import com.example.demo.dto.Response.StudentReportResponse;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/students")
    public ResponseEntity<List<StudentReportResponse>> getStudentReports(
            @RequestParam(required = false) Long classId) {
        return ResponseEntity.ok(reportService.getStudentReports(classId));
    }
}
