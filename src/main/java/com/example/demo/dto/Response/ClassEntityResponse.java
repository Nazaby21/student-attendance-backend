package com.example.demo.dto.Response;

import java.time.LocalDateTime;
import java.util.List;

public record ClassEntityResponse(
        Long id,
        String className,
        String description,
        int year,
        LocalDateTime createdDate,
        List<TeacherInfo> teachers
) {
}
