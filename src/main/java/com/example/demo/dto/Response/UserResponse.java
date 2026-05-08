package com.example.demo.dto.Response;

import com.example.demo.enumeration.Gender;
import com.example.demo.enumeration.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        String dateOfBirth,
        String phoneNumber,
        Gender gender,
        Role role,
        Long classId
) {
}
