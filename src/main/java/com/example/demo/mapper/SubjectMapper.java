package com.example.demo.mapper;

import com.example.demo.dto.Request.SubjectRequest;
import com.example.demo.dto.Response.SubjectResponse;
import com.example.demo.modal.Subject;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface SubjectMapper {

    SubjectResponse toSubjectResponse(Subject subject);

    Subject toSubjectEntity(SubjectRequest subjectRequest);
}
