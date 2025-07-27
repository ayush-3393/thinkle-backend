package com.thinkle_backend.security.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String userName);
    String extractUserName(String token);
    boolean validateToken(String token, UserDetails userDetails);
}
