package com.example.demo.mapper;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.modal.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "classId", ignore = true)
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toUserEntity(UserRequest userRequest);
}
