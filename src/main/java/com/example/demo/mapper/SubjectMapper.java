package com.example.demo.mapper;

import com.example.demo.dto.Request.SubjectRequest;
import com.example.demo.dto.Response.SubjectResponse;
import com.example.demo.modal.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface SubjectMapper {

    SubjectResponse toSubjectResponse(Subject subject);

    @Mapping(target = "id", ignore = true)
    Subject toSubjectEntity(SubjectRequest subjectRequest);
}
