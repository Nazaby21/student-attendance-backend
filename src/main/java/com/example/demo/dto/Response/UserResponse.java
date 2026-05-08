package com.example.demo.dto.Response;

import com.example.demo.enumeration.Gender;
import com.example.demo.enumeration.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String dateOfBirth,
        String phoneNumber,
        Gender gender,
        Role role,
        Long classId,
        int blacklistCount,
        double currentBlacklistPoints,
        LocalDateTime lastBlacklistReset,
        boolean blacklisted
) {
}
