package com.example.demo.mapper;

import com.example.demo.dto.Request.BlacklistRequest;
import com.example.demo.dto.Response.BlacklistResponse;
import com.example.demo.modal.Blacklist;
import com.example.demo.modal.BlacklistHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlacklistMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "classId", source = "clazz.id")
    @Mapping(target = "className", source = "clazz.className")
    BlacklistResponse toBlacklistResponse(Blacklist blacklist);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "classId", source = "clazz.id")
    @Mapping(target = "className", source = "clazz.className")
    @Mapping(target = "addedDate", source = "createdAt")
    BlacklistResponse toBlacklistResponse(BlacklistHistory history);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "clazz", ignore = true)
    @Mapping(target = "addedDate", ignore = true)
    Blacklist toBlacklistEntity(BlacklistRequest blacklistRequest);
}
