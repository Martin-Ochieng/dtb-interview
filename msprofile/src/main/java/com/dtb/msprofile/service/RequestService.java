package com.dtb.msprofile.service;

import com.dtb.msprofile.dto.request.CreateProfileRequest;
import com.dtb.msprofile.dto.request.LoginRequest;
import com.dtb.msprofile.dto.request.UpdateProfileRequest;
import com.dtb.msprofile.dto.response.ApiResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface RequestService {
    Mono<ApiResponse> createProfile(@Valid CreateProfileRequest request, Map<String, String> headers);


    Mono<ApiResponse> updateProfile(@Valid UpdateProfileRequest request, Map<String, String> headers);

    Mono<ApiResponse> login(@Valid LoginRequest request, Map<String, String> headers);
}
