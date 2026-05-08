package com.example.demo.dto.Response;

import java.time.LocalDateTime;

public record BlacklistResponse(
        Long id,
        Long studentId,
        String studentName,
        Long classId,
        String className,
        String reason,
        LocalDateTime addedDate
) {
}
