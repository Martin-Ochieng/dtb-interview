package com.dtb.mspayment.service;

import com.dtb.mspayment.dto.request.WithdrawalDeposit;
import com.dtb.mspayment.dto.request.Transfer;
import com.dtb.mspayment.dto.response.ApiResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface RequestService {
    Mono<ApiResponse> withdrawalDeposit(@Valid WithdrawalDeposit request, Map<String, String> headers);


    Mono<ApiResponse> transfer(@Valid Transfer request, Map<String, String> headers);


}
