package com.example.demo.mapper;

import com.example.demo.dto.Request.EnrollmentRequest;
import com.example.demo.dto.Response.EnrollmentResponse;
import com.example.demo.modal.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);

    Enrollment toEnrollmentEntity(EnrollmentRequest enrollmentRequest);
}
