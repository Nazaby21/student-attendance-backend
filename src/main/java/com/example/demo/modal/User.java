package com.example.demo.modal;

import com.example.demo.enumeration.Gender;
import com.example.demo.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "blacklist_count")
    private Integer blacklistCount = 0;

    @Column(name = "current_blacklist_points")
    private Double currentBlacklistPoints = 0.0;

    @Column(name = "last_blacklist_reset")
    private java.time.LocalDateTime lastBlacklistReset = java.time.LocalDateTime.now();

    @Column(name = "is_blacklisted")
    private Boolean blacklisted = false;
}
