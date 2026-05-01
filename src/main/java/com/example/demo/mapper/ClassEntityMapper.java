package com.example.demo.mapper;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.modal.ClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassEntityMapper {

    ClassEntityResponse toClassEntityResponse(ClassEntity classEntity);

    @Mapping(target = "id", ignore = true)
    ClassEntity toClassEntity(ClassEntityRequest classEntityRequest);
}
