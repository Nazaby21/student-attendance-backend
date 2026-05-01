package com.example.demo.repository;

import com.example.demo.modal.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByClazzId(Long clazzId);
    Optional<Enrollment> findByStudentIdAndClazzId(Long studentId, Long clazzId);
}
