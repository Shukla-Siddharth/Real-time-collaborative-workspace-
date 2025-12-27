package com.purplemerit.workspace.auth.service;

import com.purplemerit.workspace.auth.dto.AuthResponse;
import com.purplemerit.workspace.auth.dto.RegisterRequest;
import com.purplemerit.workspace.auth.entity.Role;
import com.purplemerit.workspace.auth.entity.User;
import com.purplemerit.workspace.auth.repository.UserRepository;
import com.purplemerit.workspace.auth.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthResponse register(RegisterRequest request) {

        // 1️⃣ Prevent duplicate users
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // 2️⃣ Create user with default OWNER role
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.OWNER)   // ✅ DEFAULT ROLE
                .build();

        // 3️⃣ Persist user
        userRepository.save(user);

        // 4️⃣ Generate JWT tokens
        return AuthResponse.builder()
                .accessToken(jwtTokenService.generateAccessToken(user))
                .refreshToken(jwtTokenService.generateRefreshToken())
                .build();
    }
}
