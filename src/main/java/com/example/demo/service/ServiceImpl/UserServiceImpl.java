package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.modal.User;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.ClassSession;
import com.example.demo.modal.Enrollment;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.BlacklistRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.ClassSessionRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassEntityRepository classRepository;
    private final AttendanceRepository attendanceRepository;
    private final BlacklistRepository blacklistRepository;
    private final ClassSessionRepository sessionRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toUserEntity(userRequest);
        if (userRequest.password() == null || userRequest.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode("Default@123"));
        } else {
            user.setPassword(passwordEncoder.encode(userRequest.password()));
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(com.example.demo.enumeration.Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setDateOfBirth(userRequest.dateOfBirth());
        user.setPhoneNumber(userRequest.phoneNumber());
        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.password()));
        }
        user.setGender(userRequest.gender());
        user.setRole(userRequest.role());
        
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 1. If Teacher: handle classes and sessions
        if (user.getRole() == com.example.demo.enumeration.Role.TEACHER) {
            // Remove from classes join table
            List<ClassEntity> classes = classRepository.findByTeachersId(id);
            for (ClassEntity clazz : classes) {
                clazz.getTeachers().remove(user);
                classRepository.save(clazz);
            }
            
            // Delete sessions where this user is the teacher
            List<ClassSession> sessions = sessionRepository.findByTeacherId(id);
            for (ClassSession session : sessions) {
                // Also delete attendance for these sessions
                List<Attendance> attendances = attendanceRepository.findBySessionId(session.getId());
                attendanceRepository.deleteAll(attendances);
            }
            sessionRepository.deleteAll(sessions);
            
            // Nullify recordedBy in Attendance records
            List<Attendance> recordedAttendances = attendanceRepository.findByRecordedById(id);
            for (Attendance attendance : recordedAttendances) {
                attendance.setRecordedBy(null);
                attendanceRepository.save(attendance);
            }
        }

        // 2. If Student: handle enrollments and attendance
        if (user.getRole() == com.example.demo.enumeration.Role.STUDENT) {
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
            for (Enrollment enrollment : enrollments) {
                List<Attendance> attendances = attendanceRepository.findByEnrollmentId(enrollment.getId());
                attendanceRepository.deleteAll(attendances);
            }
            enrollmentRepository.deleteAll(enrollments);
            blacklistRepository.findByStudentId(id).ifPresent(blacklistRepository::delete);
        }

        userRepository.deleteById(id);
    }
}
