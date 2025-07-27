package com.thinkle_backend.security.services;

import com.thinkle_backend.security.dtos.AuthResponse;
import com.thinkle_backend.security.dtos.LoginRequest;
import com.thinkle_backend.security.dtos.SignUpRequest;

public interface UserService {
    AuthResponse handleSignup(SignUpRequest signUpRequest);
    AuthResponse handleLogin(LoginRequest loginRequest);
}
