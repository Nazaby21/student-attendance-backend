package com.example.demo.mapper;

import com.example.demo.dto.Request.ClassSessionRequest;
import com.example.demo.dto.Response.ClassSessionResponse;
import com.example.demo.modal.ClassSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassSessionMapper {

    ClassSessionResponse toClassSessionResponse(ClassSession classSession);

    ClassSession toClassSessionEntity(ClassSessionRequest classSessionRequest);
}
