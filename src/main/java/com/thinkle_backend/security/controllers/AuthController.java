package com.thinkle_backend.security.controllers;

import com.thinkle_backend.dtos.responses.BaseResponse;
import com.thinkle_backend.dtos.responses.GuessResponseDto;
import com.thinkle_backend.security.dtos.AuthResponse;
import com.thinkle_backend.security.dtos.LoginRequest;
import com.thinkle_backend.security.dtos.SignUpRequest;
import com.thinkle_backend.security.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthResponse>> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        log.info("Received signup request for email: {}", signUpRequest.getEmail());

        try {
            AuthResponse response = userService.handleSignup(signUpRequest);
            log.info("User registered successfully: {}", signUpRequest.getEmail());

            return ResponseEntity.ok(BaseResponse.success(response));

        } catch (Exception e) {
            log.error("Registration failed for email: {}", signUpRequest.getEmail(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        log.info("Received login request for email: {}", loginRequest.getEmail());

        try {
            AuthResponse response = userService.handleLogin(loginRequest);
            log.info("User logged in successfully: {}", loginRequest.getEmail());

            return ResponseEntity.ok(BaseResponse.success(response));

        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running");
    }
}
