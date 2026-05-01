package com.example.demo.mapper;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.AttendanceResponse;
import com.example.demo.modal.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
    @Mapping(source = "enrollment.id", target = "enrollment")
    @Mapping(source = "session.id", target = "session")
    AttendanceResponse toAttendanceResponse(Attendance attendance);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recordedBy", ignore = true)
    @Mapping(target = "enrollment.id", source = "enrollment")
    @Mapping(target = "session.id", source = "session")
    Attendance toAttendanceEntity(AttendanceRequest attendanceRequest);
}
