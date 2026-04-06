package com.example.demo.dto.Request;

import com.example.demo.enumeration.Status;

public record AttendanceRequest(
        Long enrollment,
        Long session,
        Status status
) {
}
