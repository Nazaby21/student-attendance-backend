package com.example.demo.dto.Request;

import com.example.demo.enumeration.Gender;
import com.example.demo.enumeration.Role;

public record UserRequest(
        String name,
        String email,
        String rollNumber,
        String dateOfBirth,
        String address,
        String phoneNumber,
        String password,
        Gender gender,
        Role role
) {
}
