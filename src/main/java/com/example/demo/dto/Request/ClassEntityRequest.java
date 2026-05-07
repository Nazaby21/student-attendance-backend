package com.example.demo.dto.Request;

public record ClassEntityRequest(
        String className,
        String code,
        String section,
        String description,
        int year
) {
}
