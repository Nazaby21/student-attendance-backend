package com.example.demo.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectRequest(
        @NotBlank(message = "Subject name is required")
        String subjectName,

        @NotBlank(message = "Subject code is required")
        String code,

        @Min(value = 1, message = "Credits must be at least 1")
        Integer credits,

        @NotNull(message = "Class ID is required")
        Long classId
) {
}
