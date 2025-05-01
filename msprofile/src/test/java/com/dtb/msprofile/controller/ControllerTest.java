package com.dtb.msprofile.controller;

import com.dtb.msprofile.dto.request.CreateProfileRequest;
import com.dtb.msprofile.dto.request.LoginRequest;
import com.dtb.msprofile.dto.request.UpdateProfileRequest;
import com.dtb.msprofile.dto.response.ApiResponse;
import com.dtb.msprofile.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

class ControllerTest {
    @Mock
    private  RequestService requestService;

    @InjectMocks
    private  Controller controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        controller = new Controller(requestService);
    }

    @Test
    void createProfile() {
        ApiResponse response = new ApiResponse("0", "Request successful", "Request Executed Successfully", "TXN001", null);
        when(requestService.createProfile(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.createProfile(new CreateProfileRequest(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("0") &&
                                apiResponse.getResponseMessage().equals("Request successful") &&
                                apiResponse.getCustomerMessage().equals("Request Executed Successfully") &&
                                apiResponse.getRefId().equals("TXN001")
                )
                .verifyComplete();
    }

    @Test
    void updateProfile() {
        ApiResponse response = new ApiResponse("1", "Request failed", "Request Execution Failed", "TXN002", null);
        when(requestService.updateProfile(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.updateProfile(new UpdateProfileRequest(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("1") &&
                                apiResponse.getResponseMessage().equals("Request failed") &&
                                apiResponse.getCustomerMessage().equals("Request Execution Failed") &&
                                apiResponse.getRefId().equals("TXN002")
                )
                .verifyComplete();
    }


    @Test
    void login() {
        ApiResponse response = new ApiResponse("1", "Request failed", "Request Execution Failed", "TXN002", null);
        when(requestService.login(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.login(new LoginRequest(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("1") &&
                                apiResponse.getResponseMessage().equals("Request failed") &&
                                apiResponse.getCustomerMessage().equals("Request Execution Failed") &&
                                apiResponse.getRefId().equals("TXN002")
                )
                .verifyComplete();
    }
}