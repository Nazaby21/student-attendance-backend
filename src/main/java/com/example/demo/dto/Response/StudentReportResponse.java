package com.example.demo.dto.Response;

public record StudentReportResponse(
        Long studentId,
        String studentName,
        Long classId,
        String className,
        long present,
        long absent,
        long late,
        long permission,
        long total,
        double percentage
) {
}
