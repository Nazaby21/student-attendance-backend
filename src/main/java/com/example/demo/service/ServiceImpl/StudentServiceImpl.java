package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.enumeration.Role;
import com.example.demo.mapper.UserMapper;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.Enrollment;
import com.example.demo.modal.User;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ClassEntityRepository classRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse addStudent(UserRequest studentRequest, Long classId) {
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
    public List<UserResponse> getAllStudents() {
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        user.setName(studentRequest.name());
        user.setEmail(studentRequest.email());
        user.setRollNumber(studentRequest.rollNumber());
        user.setDateOfBirth(studentRequest.dateOfBirth());
        user.setAddress(studentRequest.address());
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
                response.rollNumber(),
                response.dateOfBirth(),
                response.address(),
                response.phoneNumber(),
                response.gender(),
                response.role(),
                classId
        );
    }

    @Override
    public void deleteStudent(Long id) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
        enrollmentRepository.deleteAll(enrollments);
        userRepository.deleteById(id);
    }
}
