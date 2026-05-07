package com.example.demo.dto.Request;

import com.example.demo.enumeration.Status;

public record AttendanceRequest(
        Long studentId,
        Long classId,
        String date,
        String timeSlot,
        Status status,
        Long recordedById,
        String remark
) {
}
