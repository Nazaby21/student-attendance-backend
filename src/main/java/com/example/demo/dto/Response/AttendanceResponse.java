package com.example.demo.dto.Response;

import com.example.demo.enumeration.Status;

public record AttendanceResponse(
        Long id,
        Long enrollment,
        Long session,
        Status status
) {
}
