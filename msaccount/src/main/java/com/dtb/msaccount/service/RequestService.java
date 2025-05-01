package com.dtb.msaccount.service;

import com.dtb.msaccount.dto.request.CreateAccountRequest;
import com.dtb.msaccount.dto.request.GetAccountsRequest;
import com.dtb.msaccount.dto.request.UpdateAccountRequest;
import com.dtb.msaccount.dto.response.ApiResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface RequestService {
    Mono<ApiResponse> createAccount(@Valid CreateAccountRequest request, Map<String, String> headers);


    Mono<ApiResponse> updateAccount(@Valid UpdateAccountRequest request, Map<String, String> headers);

    Mono<ApiResponse> getAccounts(@Valid GetAccountsRequest request, Map<String, String> headers);
}
