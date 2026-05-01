package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.AttendanceResponse;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassSession;
import com.example.demo.modal.Enrollment;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassSessionRepository sessionRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    public AttendanceResponse recordAttendance(AttendanceRequest attendanceRequest) {
        Enrollment enrollment = enrollmentRepository.findById(attendanceRequest.enrollment())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        ClassSession session = sessionRepository.findById(attendanceRequest.session())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Attendance attendance = new Attendance();
        attendance.setEnrollment(enrollment);
        attendance.setSession(session);
        attendance.setStatus(attendanceRequest.status());

        return attendanceMapper.toAttendanceResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
        return attendanceMapper.toAttendanceResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(attendanceMapper::toAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceBySession(Long sessionId) {
        return attendanceRepository.findBySessionId(sessionId).stream()
                .map(attendanceMapper::toAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByEnrollment(Long enrollmentId) {
        return attendanceRepository.findByEnrollmentId(enrollmentId).stream()
                .map(attendanceMapper::toAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
