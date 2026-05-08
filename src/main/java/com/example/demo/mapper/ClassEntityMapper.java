package com.example.demo.mapper;

import com.example.demo.dto.Request.ClassEntityRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.modal.ClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassEntityMapper {

    // teachers and createdDate are mapped manually in the service layer
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    ClassEntityResponse toClassEntityResponse(ClassEntity classEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    ClassEntity toClassEntity(ClassEntityRequest classEntityRequest);
}
