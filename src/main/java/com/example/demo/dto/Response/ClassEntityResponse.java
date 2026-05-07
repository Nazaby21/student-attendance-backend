package com.example.demo.dto.Response;

public record ClassEntityResponse(
        Long id,
        String className,
        String code,
        String section,
        String description,
        int year
) {
}
