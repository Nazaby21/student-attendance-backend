package com.example.demo.repository;

import com.example.demo.modal.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    List<ClassSession> findByClassName(String className);
    List<ClassSession> findByClassId(String classId);
    List<ClassSession> findByDate(LocalDate date);
}
