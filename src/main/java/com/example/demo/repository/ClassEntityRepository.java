package com.example.demo.repository;

import com.example.demo.modal.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {

    Optional<ClassEntity> findByName(String name);
}
