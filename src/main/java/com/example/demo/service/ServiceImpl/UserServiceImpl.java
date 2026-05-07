package com.example.demo.service.ServiceImpl;

import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.modal.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toUserEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(com.example.demo.enumeration.Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setRollNumber(userRequest.rollNumber());
        user.setDateOfBirth(userRequest.dateOfBirth());
        user.setAddress(userRequest.address());
        user.setPhoneNumber(userRequest.phoneNumber());
        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.password()));
        }
        user.setGender(userRequest.gender());
        user.setRole(userRequest.role());
        
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
