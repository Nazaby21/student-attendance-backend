package com.example.demo.mapper;

import com.example.demo.dto.Request.SubjectRequest;
import com.example.demo.dto.Response.SubjectResponse;
import com.example.demo.modal.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Mapping(target = "classId", source = "clazz.id")
    @Mapping(target = "className", source = "clazz.className")
    SubjectResponse toSubjectResponse(Subject subject);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clazz", ignore = true)
    Subject toSubjectEntity(SubjectRequest subjectRequest);
}
