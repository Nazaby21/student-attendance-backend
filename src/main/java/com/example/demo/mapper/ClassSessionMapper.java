package com.example.demo.mapper;

import com.example.demo.dto.Request.ClassSessionRequest;
import com.example.demo.dto.Response.ClassSessionResponse;
import com.example.demo.modal.ClassSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassSessionMapper {

    @Mapping(target = "clazzId", source = "clazz.id")
    @Mapping(target = "className", source = "clazz.className")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.subjectName")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.name")
    ClassSessionResponse toClassSessionResponse(ClassSession classSession);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clazz", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    ClassSession toClassSessionEntity(ClassSessionRequest classSessionRequest);
}
