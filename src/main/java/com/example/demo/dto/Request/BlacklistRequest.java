package com.example.demo.dto.Request;

public record BlacklistRequest(
        Long studentId,
        String reason
) {
}
