package com.thinkle_backend.security.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SignUpRequest {
    private String username;
    private String email;
    private String password;
}
