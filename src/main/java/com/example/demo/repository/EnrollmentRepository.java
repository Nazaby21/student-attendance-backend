package com.example.demo.repository;

import com.example.demo.modal.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByClassEntityId(Long classEntityId);
    List<Enrollment> findByStudent_IdandClassEntityId(Long studentId, Long classEntityId);
}
