package com.example.demo.mapper;

import com.example.demo.dto.Request.EnrollmentRequest;
import com.example.demo.dto.Response.EnrollmentResponse;
import com.example.demo.modal.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(source = "student.id", target = "student")
    @Mapping(source = "clazz.id", target = "clazz")
    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student.id", source = "student")
    @Mapping(target = "clazz.id", source = "clazz")
    Enrollment toEnrollmentEntity(EnrollmentRequest enrollmentRequest);
}
