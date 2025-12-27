package com.purplemerit.workspace.auth.service;

import com.purplemerit.workspace.auth.dto.*;
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

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.OWNER)
                .build();

        user.setRefreshToken(jwtTokenService.generateRefreshToken());
        userRepository.save(user);

        return buildResponse(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        user.setRefreshToken(jwtTokenService.generateRefreshToken());
        userRepository.save(user);

        return buildResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        user.setRefreshToken(jwtTokenService.generateRefreshToken());
        userRepository.save(user);

        return buildResponse(user);
    }

    public void logout(String refreshToken) {
        userRepository.findByRefreshToken(refreshToken).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });
    }

    private AuthResponse buildResponse(User user) {
        return new AuthResponse(
                jwtTokenService.generateAccessToken(user),
                user.getRefreshToken(),
                "Bearer",
                15 * 60,
                user.getRole().name()
        );
    }
}
