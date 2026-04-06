package com.example.demo.mapper;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.ClassEntityResponse;
import com.example.demo.modal.Attendance;
import com.example.demo.modal.ClassEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassEntityMapper {

    ClassEntityResponse toClassEntityResponse(Attendance attendance);

    ClassEntity toClassEntity(AttendanceRequest attendanceRequest);
}
