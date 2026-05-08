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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.security.UserDetailsImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

import com.example.demo.modal.BlacklistHistory;
import com.example.demo.enumeration.Status;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassSessionRepository sessionRepository;
    private final ClassEntityRepository classRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final com.example.demo.service.BlacklistCalculationService blacklistCalculationService;
    private final AttendanceMapper attendanceMapper;

    private void checkTeacherAssignment(Long classId) {
        if (classId == null) return;
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            boolean isTeacher = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
            
            if (isTeacher) {
                ClassEntity classEntity = classRepository.findById(classId)
                        .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Class not found"));
                boolean isAssigned = classEntity.getTeachers().stream()
                        .anyMatch(t -> t.getId() != null && t.getId().equals(userDetails.getId()));
                if (!isAssigned) {
                    throw new org.springframework.security.access.AccessDeniedException("Access Denied: You are not assigned to this class.");
                }
            }
        }
    }

    @Override
    public AttendanceResponse recordAttendance(AttendanceRequest attendanceRequest) {
        checkTeacherAssignment(attendanceRequest.classId());
        
        if (attendanceRequest.date() == null) {
            throw new com.example.demo.exception.ResourceNotFoundException("Attendance date is required");
        }

        // Validation: Cannot update future attendance
        LocalDate attendanceDate = LocalDate.parse(attendanceRequest.date());
        if (attendanceDate.isAfter(LocalDate.now())) {
            throw new com.example.demo.exception.ResourceNotFoundException("Cannot update future attendance");
        }

        // Find Enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndClazzId(
                attendanceRequest.studentId(), attendanceRequest.classId())
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Enrollment not found for student and class"));
        
        User student = enrollment.getStudent();

        // Find or Create ClassSession
        ClassSession session = sessionRepository.findByClazzIdAndDateAndTimeSlot(
                attendanceRequest.classId(), attendanceRequest.date(), attendanceRequest.timeSlot())
                .stream().findFirst()
                .orElseGet(() -> {
                    ClassSession newSession = new ClassSession();
                    newSession.setClazz(enrollment.getClazz());
                    newSession.setDate(attendanceRequest.date());
                    newSession.setTimeSlot(attendanceRequest.timeSlot() != null ? attendanceRequest.timeSlot() : "08:00 - 10:00");
                    
                    // Assign subject - try to find one assigned to this class
                    var subjects = subjectRepository.findAll().stream()
                            .filter(s -> s.getClazz() != null && s.getClazz().getId().equals(attendanceRequest.classId()))
                            .collect(Collectors.toList());
                    
                    if (subjects.isEmpty()) {
                        subjects = subjectRepository.findAll();
                    }
                    
                    if (subjects.isEmpty()) {
                        throw new com.example.demo.exception.ResourceNotFoundException("No subjects found in the system.");
                    }
                    newSession.setSubject(subjects.get(0));

                    User teacher = null;
                    if (attendanceRequest.recordedById() != null) {
                        teacher = userRepository.findById(attendanceRequest.recordedById()).orElse(null);
                    }
                    if (teacher == null) {
                        teacher = enrollment.getClazz().getTeachers().stream().findFirst().orElse(null);
                    }
                    if (teacher == null) {
                        throw new com.example.demo.exception.ResourceNotFoundException("No teacher found for this class session.");
                    }
                    newSession.setTeacher(teacher);
                    
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

        Attendance savedAttendance = attendanceRepository.save(attendance);
        
        // Trigger Blacklist Point Calculation
        blacklistCalculationService.updateStudentBlacklistPoints(student.getId());

        return mapToResponseSafe(savedAttendance);
    }

    private AttendanceResponse mapToResponseSafe(Attendance a) {
        if (a == null) return null;
        try {
            return attendanceMapper.toAttendanceResponse(a);
        } catch (Exception e) {
            // Manual fallback if mapper fails due to lazy loading issues
            return new AttendanceResponse(
                a.getId(),
                a.getEnrollment() != null && a.getEnrollment().getStudent() != null ? a.getEnrollment().getStudent().getId() : null,
                a.getEnrollment() != null && a.getEnrollment().getStudent() != null ? a.getEnrollment().getStudent().getName() : "Unknown",
                a.getEnrollment() != null && a.getEnrollment().getClazz() != null ? a.getEnrollment().getClazz().getId() : null,
                a.getEnrollment() != null && a.getEnrollment().getClazz() != null ? a.getEnrollment().getClazz().getClassName() : "N/A",
                a.getSession() != null ? a.getSession().getId() : null,
                a.getSession() != null ? a.getSession().getDate() : null,
                a.getSession() != null ? a.getSession().getTimeSlot() : null,
                a.getStatus(),
                a.getRecordedBy() != null ? a.getRecordedBy().getName() : "System",
                a.getRemark()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Attendance not found"));
        
        if (attendance.getSession() != null && attendance.getSession().getClazz() != null) {
            checkTeacherAssignment(attendance.getSession().getClazz().getId());
        }
        
        return mapToResponseSafe(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAllAttendance() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Attendance> allAttendances = attendanceRepository.findAll();

        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            boolean isTeacher = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

            if (isTeacher) {
                return allAttendances.stream()
                        .filter(a -> a.getSession() != null &&
                                a.getSession().getClazz() != null &&
                                a.getSession().getClazz().getTeachers().stream()
                                        .anyMatch(t -> t.getId() != null && t.getId().equals(userDetails.getId())))
                        .map(this::mapToResponseSafe)
                        .collect(Collectors.toList());
            }
        }

        return allAttendances.stream()
                .map(this::mapToResponseSafe)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceBySession(Long sessionId) {
        ClassSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Session not found"));
        checkTeacherAssignment(session.getClazz().getId());
        
        return attendanceRepository.findBySessionId(sessionId).stream()
                .map(this::mapToResponseSafe)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new com.example.demo.exception.ResourceNotFoundException("Enrollment not found"));
        checkTeacherAssignment(enrollment.getClazz().getId());

        return attendanceRepository.findByEnrollmentId(enrollmentId).stream()
                .map(this::mapToResponseSafe)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByClassAndDate(Long classId, String date) {
        checkTeacherAssignment(classId);
        return attendanceRepository.findBySessionClazzIdAndSessionDate(classId, date).stream()
                .map(this::mapToResponseSafe)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id).orElse(null);
        if (attendance != null && attendance.getEnrollment() != null && attendance.getEnrollment().getStudent() != null) {
            Long studentId = attendance.getEnrollment().getStudent().getId();
            attendanceRepository.deleteById(id);
            blacklistCalculationService.updateStudentBlacklistPoints(studentId);
        } else {
            attendanceRepository.deleteById(id);
        }
    }
}
