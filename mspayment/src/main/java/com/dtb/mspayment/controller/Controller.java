package com.dtb.mspayment.controller;

import com.dtb.mspayment.dto.request.WithdrawalDeposit;
import com.dtb.mspayment.dto.request.Transfer;
import com.dtb.mspayment.dto.response.ApiResponse;
import com.dtb.mspayment.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Operations related to payment transactions such as withdrawal, deposit, and transfer.")
public class Controller {

    private final RequestService requestService;

    @PostMapping("/withdrawal-deposit")
    @Operation(
            summary = "Process Withdrawal/Deposit",
            description = "This endpoint allows users to make withdrawal or deposit transactions.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Transaction successfully processed"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid request or transaction details"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public Mono<ApiResponse> withdrawalDeposit(@Valid @RequestBody WithdrawalDeposit request,
                                               @RequestHeader Map<String, String> headers) {
        return requestService.withdrawalDeposit(request, headers);
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Process Transfer",
            description = "This endpoint allows users to transfer funds between accounts.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Transfer successfully completed"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid transfer details"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public Mono<ApiResponse> transfer(@Valid @RequestBody Transfer request,
                                      @RequestHeader Map<String, String> headers) {
        return requestService.transfer(request, headers);
    }
}
