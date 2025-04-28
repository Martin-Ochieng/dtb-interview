package com.dtb.msgateway.controller;

import com.dtb.msgateway.config.Config;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final Config config;




    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Decode username:password
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);

        final String[] values = credentials.split(":", 2);

        String username = values[0];
        String password = values[1];

        // ⚡ Validate username and password (mock example)
        // Validate username and password (mock example)
        if (isValidUser(username, password)) {
            String token = generateToken(username);
            long expirationInMillis = config.getAuthConfig().getJwtExpirationMs(); // Extract the expiration in milliseconds

            // Create a map with both token and expiration time
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("expiration", expirationInMillis);

            return ResponseEntity.ok(response); // Return the response with both token and expiration
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private boolean isValidUser(String username, String password) {
        // Hardcoded validation for demo; normally query your DB or user service
        return config.getAuthConfig().getUsername().equals(username) &&
                config.getAuthConfig().getPassword().equals(password);
    }

    private String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(config.getAuthConfig().getJwtSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + config.getAuthConfig().getJwtExpirationMs()))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }


}


