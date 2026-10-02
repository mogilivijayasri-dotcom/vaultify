package com.vaultify.vaultifybackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long expirationTime =
            1000 * 60 * 60;

    public JwtService() {

        String key =
                System.getenv("VAULTIFY_JWT_SECRET");

        if (key == null || key.length() < 32) {

            throw new IllegalStateException(
                    "VAULTIFY_JWT_SECRET is not configured correctly."
            );
        }

        secretKey =
                Keys.hmacShaKeyFor(
                        key.getBytes(StandardCharsets.UTF_8)
                );
    }

    public String generateToken(String email) {

        Date now = new Date();

        Date expiry =
                new Date(
                        now.getTime()
                                + expirationTime
                );

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {

        try {

            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}