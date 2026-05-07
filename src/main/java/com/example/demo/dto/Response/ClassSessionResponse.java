package com.example.demo.dto.Response;

public record ClassSessionResponse(
        Long id,
        Long clazzId,
        String className,
        Long subjectId,
        String subjectName,
        Long teacherId,
        String teacherName,
        String date,
        String timeSlot
) {
}
