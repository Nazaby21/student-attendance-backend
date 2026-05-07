package com.example.demo.dto.Response;

public record SubjectResponse(
        Long id,
        String subjectName,
        String code,
        Integer credits,
        Long classId,
        String className
) {
}
