package com.dtb.msgateway.controller;



import com.dtb.msgateway.config.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private Config config;

    private Config mockConfig;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockConfig = new Config(
                new Config.AuthConfig(
                        "admin",
                        "5$kb)N>G1%oG58z",
                        3600000,  // 1 hour in milliseconds
                        "MySuperSecretMySuperSecretMySuperSecret"

                )
        );


        authController = new AuthController(config);
    }

    @Test
    void testLoginSuccess() {
        String authHeader = "Basic " + new String(Base64.getEncoder().encode("admin:5$kb)N>G1%oG58z".getBytes()));

        when(config.getAuthConfig()).thenReturn(mockConfig.getAuthConfig());

        Assertions.assertNotNull(authController.login(authHeader));

    }

    @Test
    void testLoginUnauthorized() {
        String authHeader = "Basic " + new String(Base64.getEncoder().encode("invalidUser:wrongPassword".getBytes()));
        when(config.getAuthConfig()).thenReturn(mockConfig.getAuthConfig());
        ResponseEntity<?> response = authController.login(authHeader);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLoginMissingAuthorizationHeader() {

        ResponseEntity<?> response = authController.login(null);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLoginInvalidAuthorizationHeader() {

        ResponseEntity<?> response = authController.login("InvalidHeader");

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
