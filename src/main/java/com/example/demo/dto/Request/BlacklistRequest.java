package com.example.demo.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BlacklistRequest(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Class ID is required")
        Long classId,

        @NotBlank(message = "Reason is required")
        String reason
) {
}
