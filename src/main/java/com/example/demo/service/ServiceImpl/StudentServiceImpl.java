package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.enumeration.Role;
import com.example.demo.mapper.UserMapper;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.Enrollment;
import com.example.demo.modal.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.BlacklistRepository;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassEntityRepository classRepository;
    private final AttendanceRepository attendanceRepository;
    private final BlacklistRepository blacklistRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) auth.getPrincipal();
        }
        throw new RuntimeException("Unauthorized");
    }

    /**
     * For teachers: validate they are assigned to the target class.
     * Queries ALL classes assigned to the teacher (supports multi-class teachers).
     */
    private void validateTeacherAccess(Long targetClassId) {
        if (targetClassId == null) return;

        UserDetailsImpl user = getAuthenticatedUser();
        boolean isTeacher = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

        if (isTeacher) {
            // Query all classes assigned to this teacher — supports multi-class assignment
            List<ClassEntity> assignedClasses = classRepository.findByTeachersId(user.getId());
            boolean isAssigned = assignedClasses.stream()
                    .anyMatch(c -> c.getId() != null && c.getId().equals(targetClassId));
            if (!isAssigned) {
                throw new RuntimeException("Access Denied: You can only manage your assigned class.");
            }
        }
    }

    @Override
    @Transactional
    public UserResponse addStudent(UserRequest studentRequest, Long classId) {
        validateTeacherAccess(classId);

        User user = userMapper.toUserEntity(studentRequest);
        user.setRole(Role.STUDENT);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("Student@123")); // Default password
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);

        if (classId != null) {
            ClassEntity clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(savedUser);
            enrollment.setClazz(clazz);
            enrollmentRepository.save(enrollment);
        }

        return mapToResponse(savedUser, classId);
    }

    @Override
    public UserResponse getStudentById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Long classId = enrollmentRepository.findByStudentId(user.getId()).stream()
                .findFirst()
                .map(e -> e.getClazz().getId())
                .orElse(null);

        if (classId != null) {
            validateTeacherAccess(classId);
        }

        return mapToResponse(user, classId);
    }

    @Override
    public List<UserResponse> getAllStudents() {
        UserDetailsImpl authUser = getAuthenticatedUser();
        boolean isTeacher = authUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

        if (isTeacher) {
            // Get all class IDs assigned to this teacher
            Set<Long> teacherClassIds = classRepository.findByTeachersId(authUser.getId()).stream()
                    .map(ClassEntity::getId)
                    .collect(Collectors.toSet());

            return userRepository.findByRole(Role.STUDENT).stream()
                    .map(user -> {
                        Long classId = enrollmentRepository.findByStudentId(user.getId()).stream()
                                .findFirst()
                                .map(e -> e.getClazz().getId())
                                .orElse(null);
                        return mapToResponse(user, classId);
                    })
                    .filter(response -> response.classId() != null && teacherClassIds.contains(response.classId()))
                    .collect(Collectors.toList());
        }

        return userRepository.findByRole(Role.STUDENT).stream()
                .map(user -> {
                    Long classId = enrollmentRepository.findByStudentId(user.getId()).stream()
                            .findFirst()
                            .map(e -> e.getClazz().getId())
                            .orElse(null);
                    return mapToResponse(user, classId);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateStudent(Long id, UserRequest studentRequest, Long classId) {
        validateTeacherAccess(classId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        user.setName(studentRequest.name());
        user.setEmail(studentRequest.email());
        user.setDateOfBirth(studentRequest.dateOfBirth());
        user.setPhoneNumber(studentRequest.phoneNumber());

        if (studentRequest.password() != null && !studentRequest.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(studentRequest.password()));
        }

        User savedUser = userRepository.save(user);

        if (classId != null) {
            // Update enrollment if needed
            Enrollment enrollment = enrollmentRepository.findByStudentId(id).stream().findFirst()
                    .orElse(new Enrollment());
            ClassEntity clazz = classRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            enrollment.setStudent(savedUser);
            enrollment.setClazz(clazz);
            enrollmentRepository.save(enrollment);
        }

        return mapToResponse(savedUser, classId);
    }

    private UserResponse mapToResponse(User user, Long classId) {
        UserResponse response = userMapper.toUserResponse(user);
        return new UserResponse(
                response.id(),
                response.name(),
                response.email(),
                response.dateOfBirth(),
                response.phoneNumber(),
                response.gender(),
                response.role(),
                classId,
                response.blacklistCount(),
                response.currentBlacklistPoints(),
                response.lastBlacklistReset(),
                response.blacklisted()
        );
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        UserResponse sr = getStudentById(id);
        if (sr.classId() != null) {
            validateTeacherAccess(sr.classId());
        }

        // 1. Delete Attendance records for all enrollments of this student
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
        for (Enrollment enrollment : enrollments) {
            List<Attendance> attendances = attendanceRepository.findByEnrollmentId(enrollment.getId());
            attendanceRepository.deleteAll(attendances);
        }

        // 2. Delete Blacklist entry if exists
        blacklistRepository.findByStudentId(id).ifPresent(blacklistRepository::delete);

        // 3. Delete Enrollments
        enrollmentRepository.deleteAll(enrollments);

        // 4. Delete the User record
        userRepository.deleteById(id);
    }
}
