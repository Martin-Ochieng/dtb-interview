package com.dtb.msgateway.controller;

import com.dtb.msgateway.config.Config;
import com.dtb.msgateway.util.logging.Logging;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Handles user authentication and JWT token generation")
public class AuthController {

    private final Config config;
    private final Logging logging = new Logging();

    @GetMapping("/login")
    @Operation(
            summary = "Login to generate JWT",
            description = "Authenticates the user using Basic Authentication and generates a JWT token if valid.",
            tags = {"Authentication"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "JWT issued successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            description = "Response containing the JWT token and expiration time",
                                            example = "{\"token\": \"jwt-token-here\", \"expiration\": 3600000}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized: Invalid or missing Authorization header"
                    )
            }
    )
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
        long startTime = System.currentTimeMillis();
        String transactionId = UUID.randomUUID().toString(); // Or use a traceable header if available

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logging.setLogLevel("warn")
                    .setTransactionID(transactionId)
                    .setProcess("Login")
                    .setRequest("Missing or invalid Authorization header")
                    .setResponse("401 Unauthorized")
                    .setResponseMsg("Login failed")
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Decode username:password
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);

        final String[] values = credentials.split(":", 2);

        String username = values[0];
        String password = values[1];

        if (isValidUser(username, password)) {
            String token = generateToken(username);
            long expirationInMillis = config.getAuthConfig().getJwtExpirationMs();

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("expiration", expirationInMillis);

            logging.setLogLevel("info")
                    .setTransactionID(transactionId)
                    .setProcess("Login")
                    .setRequest("username=" + username)
                    .setResponse("JWT issued")
                    .setResponseMsg("Login successful")
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();

            return ResponseEntity.ok(response);
        } else {
            logging.setLogLevel("warn")
                    .setTransactionID(transactionId)
                    .setProcess("Login")
                    .setRequest("username=" + username)
                    .setResponse("401 Unauthorized")
                    .setResponseMsg("Login failed: invalid credentials")
                    .setProcessDuration(System.currentTimeMillis() - startTime)
                    .write();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private boolean isValidUser(String username, String password) {
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
