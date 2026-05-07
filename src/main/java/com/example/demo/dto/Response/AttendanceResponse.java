package com.example.demo.dto.Response;

import com.example.demo.enumeration.Status;

public record AttendanceResponse(
        Long id,
        Long studentId,
        String studentName,
        Long classId,
        String className,
        Long sessionId,
        String sessionDate,
        String sessionTimeSlot,
        Status status,
        String recordedBy,
        String remark
) {
}
