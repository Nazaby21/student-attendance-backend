package com.example.demo.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ClassEntityRequest(
        @NotBlank(message = "Class name is required")
        String className,

        String description,

        @Min(value = 2000, message = "Year must be at least 2000")
        int year,

        List<Long> teacherIds
) {
}
