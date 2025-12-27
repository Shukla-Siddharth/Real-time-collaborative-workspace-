package com.purplemerit.workspace.auth.security;

import com.purplemerit.workspace.auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.access-token-expiry-minutes}")
    private long accessTokenExpiryMinutes;

    @Value("${security.jwt.refresh-token-expiry-days}")
    private long refreshTokenExpiryDays;

    public String generateAccessToken(User user) {

        return Jwts.builder()
                .setSubject(user.getId().toString())           // User identity
                .claim("email", user.getEmail())               // Useful metadata
                .claim("role", user.getRole().name())          // ✅ RBAC
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis()
                                + accessTokenExpiryMinutes * 60 * 1000)
                )
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateRefreshToken() {

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis()
                                + refreshTokenExpiryDays * 24 * 60 * 60 * 1000)
                )
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
}
