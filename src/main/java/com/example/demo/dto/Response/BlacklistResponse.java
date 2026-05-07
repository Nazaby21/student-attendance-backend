package com.example.demo.dto.Response;

import java.time.LocalDateTime;

public record BlacklistResponse(
        Long id,
        Long studentId,
        String studentName,
        String reason,
        LocalDateTime addedDate
) {
}
