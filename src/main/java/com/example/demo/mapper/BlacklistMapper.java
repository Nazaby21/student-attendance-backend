package com.example.demo.mapper;

import com.example.demo.dto.Request.BlacklistRequest;
import com.example.demo.dto.Response.BlacklistResponse;
import com.example.demo.modal.Blacklist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlacklistMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    BlacklistResponse toBlacklistResponse(Blacklist blacklist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "addedDate", ignore = true)
    Blacklist toBlacklistEntity(BlacklistRequest blacklistRequest);
}
