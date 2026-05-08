package com.example.demo.modal;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "blacklist_history")
public class BlacklistHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity clazz;

    private double points;

    @Column(name = "blacklist_number")
    private int blacklistNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String reason;
}
