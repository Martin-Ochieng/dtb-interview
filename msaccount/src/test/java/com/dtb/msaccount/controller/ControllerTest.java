package com.dtb.msaccount.controller;

import com.dtb.msaccount.dto.request.CreateAccountRequest;
import com.dtb.msaccount.dto.request.GetAccountsRequest;
import com.dtb.msaccount.dto.request.UpdateAccountRequest;
import com.dtb.msaccount.dto.response.ApiResponse;
import com.dtb.msaccount.service.RequestService;
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
    void createAccount() {
        ApiResponse response = new ApiResponse("0", "Request successful", "Request Executed Successfully", "TXN001", null);
        when(requestService.createAccount(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.createAccount(new CreateAccountRequest(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("0") &&
                                apiResponse.getResponseMessage().equals("Request successful") &&
                                apiResponse.getCustomerMessage().equals("Request Executed Successfully") &&
                                apiResponse.getRefId().equals("TXN001")
                )
                .verifyComplete();
    }

    @Test
    void updateAccount() {
        ApiResponse response = new ApiResponse("1", "Request failed", "Request Execution Failed", "TXN002", null);
        when(requestService.updateAccount(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.updateAccount(new UpdateAccountRequest(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("1") &&
                                apiResponse.getResponseMessage().equals("Request failed") &&
                                apiResponse.getCustomerMessage().equals("Request Execution Failed") &&
                                apiResponse.getRefId().equals("TXN002")
                )
                .verifyComplete();
    }


    @Test
    void getAccounts() {
        ApiResponse response = new ApiResponse("1", "Request failed", "Request Execution Failed", "TXN002", null);
        when(requestService.getAccounts(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.getAccounts(new GetAccountsRequest(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("1") &&
                                apiResponse.getResponseMessage().equals("Request failed") &&
                                apiResponse.getCustomerMessage().equals("Request Execution Failed") &&
                                apiResponse.getRefId().equals("TXN002")
                )
                .verifyComplete();
    }
}