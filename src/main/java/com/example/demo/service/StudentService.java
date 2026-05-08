package com.example.demo.service;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import java.util.List;

public interface StudentService {
    UserResponse addStudent(UserRequest studentRequest, Long classId);
    UserResponse getStudentById(Long id);
    List<UserResponse> getAllStudents();
    UserResponse updateStudent(Long id, UserRequest studentRequest, Long classId);
    void deleteStudent(Long id);
}
