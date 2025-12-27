package com.purplemerit.workspace.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
//@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;   // "Bearer"
    private long expiresIn;     // seconds
    private String role;

    public AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            String role
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.role = role;
    }
}
