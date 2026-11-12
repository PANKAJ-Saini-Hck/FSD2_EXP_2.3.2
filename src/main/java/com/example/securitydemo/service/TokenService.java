package com.example.securitydemo.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.securitydemo.entity.RefreshToken;
import com.example.securitydemo.repository.RefreshTokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    private final RefreshTokenRepository repository;
    private final AesEncryptionService encryptionService;

    private final String JWT_SECRET =
            "my-super-secret-key-for-jwt-token-generation-123456";

    private final SecretKey key =
            Keys.hmacShaKeyFor(
                    JWT_SECRET.getBytes(StandardCharsets.UTF_8));

    public TokenService(
            RefreshTokenRepository repository,
            AesEncryptionService encryptionService) {

        this.repository = repository;
        this.encryptionService = encryptionService;
    }

    public String generateAccessToken(String username) {

        long expiry =
                System.currentTimeMillis() + (15 * 60 * 1000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(expiry))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String username)
            throws Exception {

        String refreshToken = UUID.randomUUID().toString();

        String encryptedToken =
                encryptionService.encrypt(refreshToken);

        RefreshToken token =
                repository.findByUsername(username)
                        .orElse(new RefreshToken());

        token.setUsername(username);
        token.setEncryptedToken(encryptedToken);
        token.setExpiryTime(
                LocalDateTime.now().plusDays(7));

        repository.save(token);

        return refreshToken;
    }

    public boolean validateRefreshToken(
            String username,
            String providedToken) throws Exception {

        RefreshToken stored =
                repository.findByUsername(username)
                        .orElse(null);

        if (stored == null) {
            return false;
        }

        if (stored.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            return false;
        }

        String decrypted =
                encryptionService.decrypt(
                        stored.getEncryptedToken());

        return decrypted.equals(providedToken);
    }

    public void revokeToken(String username) {

        repository.findByUsername(username)
                .ifPresent(repository::delete);
    }
}