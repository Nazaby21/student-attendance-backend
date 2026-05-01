package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.EnrollmentRequest;
import com.example.demo.dto.Response.EnrollmentResponse;
import com.example.demo.mapper.EnrollmentMapper;
import com.example.demo.modal.ClassEntity;
import com.example.demo.modal.Enrollment;
import com.example.demo.modal.User;
import com.example.demo.repository.ClassEntityRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ClassEntityRepository classRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public EnrollmentResponse enrollStudent(EnrollmentRequest enrollmentRequest) {
        User student = userRepository.findById(enrollmentRequest.student())
                .orElseThrow(() -> new RuntimeException("Student not found"));
        ClassEntity classEntity = classRepository.findById(enrollmentRequest.clazz())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setClazz(classEntity);

        return enrollmentMapper.toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        return enrollmentMapper.toEnrollmentResponse(enrollment);
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponse> getEnrollmentsByClass(Long classId) {
        return enrollmentRepository.findByClazzId(classId).stream()
                .map(enrollmentMapper::toEnrollmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void unenrollStudent(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
