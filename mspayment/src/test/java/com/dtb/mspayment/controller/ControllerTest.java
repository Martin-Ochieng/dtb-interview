package com.dtb.mspayment.controller;

import com.dtb.mspayment.dto.request.WithdrawalDeposit;
import com.dtb.mspayment.dto.request.Transfer;
import com.dtb.mspayment.dto.response.ApiResponse;
import com.dtb.mspayment.service.RequestService;
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
    void withdrawalDeposit() {
        ApiResponse response = new ApiResponse("0", "Request successful", "Request Executed Successfully", "TXN001", null);
        when(requestService.withdrawalDeposit(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.withdrawalDeposit(new WithdrawalDeposit(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("0") &&
                                apiResponse.getResponseMessage().equals("Request successful") &&
                                apiResponse.getCustomerMessage().equals("Request Executed Successfully") &&
                                apiResponse.getRefId().equals("TXN001")
                )
                .verifyComplete();
    }

    @Test
    void transfer() {
        ApiResponse response = new ApiResponse("1", "Request failed", "Request Execution Failed", "TXN002", null);
        when(requestService.transfer(any(), anyMap())).thenReturn(Mono.just(response));

        StepVerifier.create(controller.transfer(new Transfer(), new HashMap<>()))
                .expectNextMatches(apiResponse ->
                        apiResponse.getResponseCode().equals("1") &&
                                apiResponse.getResponseMessage().equals("Request failed") &&
                                apiResponse.getCustomerMessage().equals("Request Execution Failed") &&
                                apiResponse.getRefId().equals("TXN002")
                )
                .verifyComplete();
    }


}