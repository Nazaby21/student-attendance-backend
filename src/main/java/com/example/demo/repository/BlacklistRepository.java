package com.example.demo.repository;

import com.example.demo.modal.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {
    Optional<Blacklist> findByStudentId(Long studentId);
    boolean existsByStudentId(Long studentId);
}
