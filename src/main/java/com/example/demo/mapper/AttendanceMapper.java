package com.example.demo.mapper;

import com.example.demo.dto.Response.AttendanceResponse;
import com.example.demo.modal.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(target = "studentId", source = "enrollment.student.id")
    @Mapping(target = "studentName", source = "enrollment.student.name")
    @Mapping(target = "classId", source = "enrollment.clazz.id")
    @Mapping(target = "className", source = "enrollment.clazz.className")
    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "sessionDate", source = "session.date")
    @Mapping(target = "sessionTimeSlot", source = "session.timeSlot")
    @Mapping(target = "recordedBy", source = "recordedBy.name")
    AttendanceResponse toAttendanceResponse(Attendance attendance);
}
