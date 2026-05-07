package com.example.demo.dto.Request;

public record SubjectRequest(
        String subjectName,
        String code,
        Integer credits,
        Long classId
) {
}
