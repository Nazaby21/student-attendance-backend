package com.example.demo.config;

import com.example.demo.enumeration.Role;
import com.example.demo.modal.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final jakarta.persistence.EntityManager entityManager;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void run(String... args) throws Exception {
        // Fix for stale PostgreSQL CHECK constraints when Enums are updated
        String[] constraints = {
            "attendances_status_check",
            "users_role_check",
            "users_gender_check"
        };
        
        for (String constraint : constraints) {
            try {
                entityManager.createNativeQuery("ALTER TABLE attendances DROP CONSTRAINT IF EXISTS " + constraint).executeUpdate();
                entityManager.createNativeQuery("ALTER TABLE users DROP CONSTRAINT IF EXISTS " + constraint).executeUpdate();
                System.out.println("Cleaned up stale constraint: " + constraint);
            } catch (Exception e) {
                // Ignore errors if table or constraint doesn't exist
            }
        }

        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@example.com / admin123");
        }

        if (!userRepository.existsByEmail("teacher@example.com")) {
            User teacher = new User();
            teacher.setName("Teacher User");
            teacher.setEmail("teacher@example.com");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setRole(Role.TEACHER);
            userRepository.save(teacher);
            System.out.println("Default teacher user created: teacher@example.com / teacher123");
        }
    }
}
