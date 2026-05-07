package com.example.demo.service;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(com.example.demo.enumeration.Role role);
    UserResponse updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
}
