package com.example.demo.mapper;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.modal.User;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    User toUserEntity(UserRequest userRequest);
}
