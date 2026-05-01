package com.example.demo.mapper;

import com.example.demo.dto.Request.ClassSessionRequest;
import com.example.demo.dto.Response.ClassSessionResponse;
import com.example.demo.modal.ClassSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassSessionMapper {

    @Mapping(source = "clazz.id", target = "clazz")
    @Mapping(source = "subject.id", target = "subject")
    @Mapping(source = "teacher.id", target = "teacher")
    ClassSessionResponse toClassSessionResponse(ClassSession classSession);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clazz.id", source = "clazz")
    @Mapping(target = "subject.id", source = "subject")
    @Mapping(target = "teacher.id", source = "teacher")
    ClassSession toClassSessionEntity(ClassSessionRequest classSessionRequest);
}
