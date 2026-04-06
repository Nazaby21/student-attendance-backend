package com.example.demo.mapper;

import com.example.demo.dto.Request.AttendanceRequest;
import com.example.demo.dto.Response.AttendanceResponse;
import com.example.demo.modal.Attendance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceResponse toAttendanceResponse(Attendance attendance);

    Attendance toAttendanceEntity(AttendanceRequest attendanceRequest);
}
