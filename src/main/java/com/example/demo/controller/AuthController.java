package com.example.demo.controller;

import com.example.demo.dto.Request.LoginRequest;
import com.example.demo.dto.Request.UserRequest;
import com.example.demo.dto.Response.JwtResponse;
import com.example.demo.dto.Response.UserResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final com.example.demo.service.RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Check if user is a student
        boolean isStudent = userDetails.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_STUDENT"));
        
        if (isStudent) {
            log.warn("Blocked login attempt for student: {}", loginRequest.email());
            throw new org.springframework.security.access.AccessDeniedException("Students are not allowed to log in to the admin system.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        com.example.demo.modal.RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        log.info("User logged in successfully: {}", loginRequest.email());
        return ResponseEntity.ok(new JwtResponse(jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getName(),
                userDetails.getClassId(),
                roles));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody com.example.demo.dto.Request.TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(com.example.demo.modal.RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    return ResponseEntity.ok(new com.example.demo.dto.Response.TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new com.example.demo.exception.AppException("Refresh token is not in database!", org.springframework.http.HttpStatus.UNAUTHORIZED));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@jakarta.validation.Valid @RequestBody UserRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new com.example.demo.exception.AppException("Email is already in use!", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        UserResponse userResponse = userService.createUser(signUpRequest);
        return ResponseEntity.ok(userResponse);
    }
}
