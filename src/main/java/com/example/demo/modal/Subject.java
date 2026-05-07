package com.example.demo.modal;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name")
    private String subjectName;

    private String code;

    private Integer credits;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassEntity clazz;


}
