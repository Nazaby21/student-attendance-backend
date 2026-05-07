package com.example.demo.controller;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.AttendanceResponse;
import com.example.demo.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(@RequestBody AttendanceRequest attendanceRequest) {
        return ResponseEntity.ok(attendanceService.recordAttendance(attendanceRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySession(sessionId));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEnrollment(enrollmentId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByClassAndDate(
            @RequestParam Long classId,
            @RequestParam String date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClassAndDate(classId, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
