package com.example.demo.modal;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity clazz;

}
