package com.example.securitydemo.controller;

import com.example.securitydemo.dto.LoginRequest;
import com.example.securitydemo.dto.TokenResponse;
import com.example.securitydemo.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (!"admin".equals(request.getUsername())
                || !"admin123".equals(request.getPassword())) {
            return ResponseEntity.status(401)
                    .body("Invalid username or password");
        }

        try {
            String accessToken =
                    tokenService.generateAccessToken(request.getUsername());

            String refreshToken =
                    tokenService.generateRefreshToken(request.getUsername());

            return ResponseEntity.ok(
                    new TokenResponse(accessToken, refreshToken)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Token generation failed");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestParam String username,
            @RequestParam String refreshToken) {

        try {
            boolean valid =
                    tokenService.validateRefreshToken(username, refreshToken);

            if (!valid) {
                return ResponseEntity.status(401)
                        .body("Invalid or expired refresh token");
            }

            String newAccessToken =
                    tokenService.generateAccessToken(username);

            String newRefreshToken =
                    tokenService.generateRefreshToken(username);

            return ResponseEntity.ok(
                    new TokenResponse(newAccessToken, newRefreshToken)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Refresh failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestParam String username) {

        tokenService.revokeToken(username);

        return ResponseEntity.ok(
                "Refresh token revoked successfully"
        );
    }
}