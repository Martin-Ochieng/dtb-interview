package com.dtb.msprofile.controller;

import com.dtb.msprofile.dto.request.CreateProfileRequest;
import com.dtb.msprofile.dto.request.LoginRequest;
import com.dtb.msprofile.dto.request.UpdateProfileRequest;
import com.dtb.msprofile.dto.response.ApiResponse;
import com.dtb.msprofile.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Operations", description = "APIs for managing user profiles including creation, updates, and login.")
public class Controller {

    private final RequestService requestService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new user profile",
            description = "This endpoint allows the creation of a new user profile with the necessary information.",
            tags = {"Profiles"}
    )
    public Mono<ApiResponse> createProfile(@Valid @RequestBody CreateProfileRequest request,
                                           @RequestHeader Map<String, String> headers) {
        return requestService.createProfile(request, headers);
    }

    @PostMapping("/update")
    @Operation(
            summary = "Update an existing user profile",
            description = "This endpoint allows updating details of an existing user profile.",
            tags = {"Profiles"}
    )
    public Mono<ApiResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                           @RequestHeader Map<String, String> headers) {
        return requestService.updateProfile(request, headers);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login a user",
            description = "This endpoint handles user login and authentication.",
            tags = {"Profiles"}
    )
    public Mono<ApiResponse> login(@Valid @RequestBody LoginRequest request,
                                   @RequestHeader Map<String, String> headers) {
        return requestService.login(request, headers);
    }
}
