package com.example.demo.repository;

import com.example.demo.enumeration.Status;
import com.example.demo.modal.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEnrollmentId(Long enrollmentId);
    List<Attendance> findBySessionId(Long sessionId);
    List<Attendance> findByRecordedById(Long recordedById);
    Optional<Attendance> findByEnrollmentIdAndSessionId(Long enrollmentId, Long sessionId);
    List<Attendance> findByStatus(Status status);
    List<Attendance> findBySessionClazzIdAndSessionDate(Long classId, String date);
    List<Attendance> findByEnrollmentStudentId(Long studentId);
    List<Attendance> findByEnrollmentStudentIdAndSessionDateGreaterThanEqual(Long studentId, String date);
    Optional<Attendance> findByEnrollmentStudentIdAndSessionDate(Long studentId, String date);
}
