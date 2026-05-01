package com.example.demo.service;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.AttendanceResponse;

import java.util.List;

public interface AttendanceService {
    AttendanceResponse recordAttendance(AttendanceRequest attendanceRequest);
    AttendanceResponse getAttendanceById(Long id);
    List<AttendanceResponse> getAllAttendance();
    List<AttendanceResponse> getAttendanceBySession(Long sessionId);
    List<AttendanceResponse> getAttendanceByEnrollment(Long enrollmentId);
    void deleteAttendance(Long id);
}
