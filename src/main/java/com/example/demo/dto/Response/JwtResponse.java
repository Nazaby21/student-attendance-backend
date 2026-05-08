package com.example.demo.dto.Response;

import java.util.List;

public record JwtResponse(
        String token,
        String refreshToken,
        Long id,
        String username,
        String email,
        String name,
        Long classId,
        java.util.List<String> roles
) {
}
