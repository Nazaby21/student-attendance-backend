package com.example.demo.repository;

import com.example.demo.modal.BlacklistHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlacklistHistoryRepository extends JpaRepository<BlacklistHistory, Long> {
    List<BlacklistHistory> findByStudentId(Long studentId);
    List<BlacklistHistory> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
