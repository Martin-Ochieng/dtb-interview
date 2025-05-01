package com.dtb.msaccount.controller;

import com.dtb.msaccount.dto.request.CreateAccountRequest;
import com.dtb.msaccount.dto.request.GetAccountsRequest;
import com.dtb.msaccount.dto.request.UpdateAccountRequest;
import com.dtb.msaccount.dto.response.ApiResponse;
import com.dtb.msaccount.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Operations related to account creation, update, and retrieval.")
public class Controller {

    private final RequestService requestService;

    @PostMapping("/create")
    @Operation(
            summary = "Create a new account",
            description = "This endpoint allows the creation of a new account with the provided account details.",
            tags = {"Account Management"}
    )
    public Mono<ApiResponse> createAccount(@Valid @RequestBody CreateAccountRequest request,
                                           @RequestHeader Map<String, String> headers) {
        return requestService.createAccount(request, headers);
    }

    @PostMapping("/update")
    @Operation(
            summary = "Update an existing account",
            description = "This endpoint allows the update of an existing account's details.",
            tags = {"Account Management"}
    )
    public Mono<ApiResponse> updateAccount(@Valid @RequestBody UpdateAccountRequest request,
                                           @RequestHeader Map<String, String> headers) {
        return requestService.updateAccount(request, headers);
    }

    @PostMapping("/get-all")
    @Operation(
            summary = "Get all accounts",
            description = "This endpoint retrieves all available accounts.",
            tags = {"Account Management"}
    )
    public Mono<ApiResponse> getAccounts(@Valid @RequestBody GetAccountsRequest request,
                                         @RequestHeader Map<String, String> headers) {
        return requestService.getAccounts(request, headers);
    }
}
