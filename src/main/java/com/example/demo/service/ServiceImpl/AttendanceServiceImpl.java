package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.AttendanceResponse;
import com.example.demo.mapper.AttendanceMapper;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.ClassSession;
import com.example.demo.modal.Enrollment;
import com.example.demo.modal.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassSessionRepository sessionRepository;
    private final ClassEntityRepository classRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceMapper attendanceMapper;

    @Override
    @Transactional
    public AttendanceResponse recordAttendance(AttendanceRequest attendanceRequest) {
        // Validation: Cannot update future attendance
        LocalDate attendanceDate = LocalDate.parse(attendanceRequest.date());
        if (attendanceDate.isAfter(LocalDate.now())) {
            throw new RuntimeException("Cannot update future attendance");
        }

        // Find Enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndClazzId(
                attendanceRequest.studentId(), attendanceRequest.classId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found for student and class"));

        // Find or Create ClassSession
        ClassSession session = sessionRepository.findByClazzIdAndDateAndTimeSlot(
                attendanceRequest.classId(), attendanceRequest.date(), attendanceRequest.timeSlot())
                .stream().findFirst()
                .orElseGet(() -> {
                    ClassSession newSession = new ClassSession();
                    newSession.setClazz(enrollment.getClazz());
                    newSession.setDate(attendanceRequest.date());
                    newSession.setTimeSlot(attendanceRequest.timeSlot());
                    // For simplicity, we assign a default subject and teacher if not found
                    // In a real app, these should be provided or pre-configured
                    newSession.setSubject(subjectRepository.findAll().stream().findFirst().orElse(null));
                    newSession.setTeacher(userRepository.findById(attendanceRequest.recordedById()).orElse(null));
                    return sessionRepository.save(newSession);
                });

        User recordedBy = null;
        if (attendanceRequest.recordedById() != null) {
            recordedBy = userRepository.findById(attendanceRequest.recordedById()).orElse(null);
        }

        // Check if attendance already exists for this enrollment and session
        Attendance attendance = attendanceRepository.findByEnrollmentIdAndSessionId(
                enrollment.getId(), session.getId())
                .orElse(new Attendance());

        attendance.setEnrollment(enrollment);
        attendance.setSession(session);
        attendance.setStatus(attendanceRequest.status());
        attendance.setRecordedBy(recordedBy);
        attendance.setRemark(attendanceRequest.remark());

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
    public List<AttendanceResponse> getAttendanceByClassAndDate(Long classId, String date) {
        return attendanceRepository.findBySessionClazzIdAndSessionDate(classId, date).stream()
                .map(attendanceMapper::toAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}
