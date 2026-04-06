package com.example.demo.dto.Request;

public record ClassSessionRequest(
        Long clazz,
        Long subject,
        Long teacher
) {
}
