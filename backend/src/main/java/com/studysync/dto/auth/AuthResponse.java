package com.studysync.dto.auth;

import com.studysync.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UserDto user;
    private JwtTokenProvider.TokenPair tokens;
    private boolean requiresTwoFactor;
}



