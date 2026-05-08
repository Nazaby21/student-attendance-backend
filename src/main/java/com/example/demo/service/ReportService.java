package com.example.demo.service;

import com.example.demo.dto.Response.StudentReportResponse;
import java.util.List;

public interface ReportService {
    List<StudentReportResponse> getStudentReports(Long classId);
}
