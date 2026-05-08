package com.example.demo.dto.Request;

import com.example.demo.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(
        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Class ID is required")
        Long classId,

        @NotBlank(message = "Date is required")
        String date,

        String timeSlot,

        @NotNull(message = "Status is required")
        Status status,

        Long recordedById,
        String remark
) {
}
