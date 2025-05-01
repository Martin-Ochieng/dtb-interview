package com.dtb.msaccount.service.impl;

import com.dtb.msaccount.config.Configs;
import com.dtb.msaccount.data.entity.Account;
import com.dtb.msaccount.data.repo.AccountRepository;
import com.dtb.msaccount.dto.request.CreateAccountRequest;
import com.dtb.msaccount.dto.request.GetAccountsRequest;
import com.dtb.msaccount.dto.request.UpdateAccountRequest;
import com.dtb.msaccount.dto.response.ApiResponse;
import com.dtb.msaccount.service.RequestService;
import com.dtb.msaccount.util.logging.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final AccountRepository accountRepository;
    private final Configs configs;


    private final Logging logging = new Logging();

    @Override
    public Mono<ApiResponse> createAccount(CreateAccountRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();

        return accountRepository.findByProfileId(request.getProfileId())
                .filter(account -> account.getAccountType().equalsIgnoreCase(request.getAccountType()))
                .hasElements()
                .flatMap(exists -> {
                    if (exists) {
                        ApiResponse response = new ApiResponse(
                                configs.getResponseConfig().getFailedResponseCode(),
                                request.getAccountType() + " : "+ configs.getResponseConfig().getAccountExistsResponseMessage(),
                                request.getAccountType() + " : "+ configs.getResponseConfig().getAccountExistsCustomerMessage(),
                                request.getRefId(),
                                null
                        );

                        logging.setLogLevel("info")
                                .setTransactionID(request.getRefId())
                                .setProcess("Create Account")
                                .setRequest(request)
                                .setResponse(response)
                                .setResponseMsg(response.getResponseMessage())
                                .setProcessDuration(System.currentTimeMillis() - startTime)
                                .write();

                        return Mono.just(response);
                    } else {
                        Account newAccount = Account.builder()
                                .profileId(request.getProfileId())
                                .balance(BigDecimal.ZERO)
                                .status("ACTIVE")
                                .accountType(request.getAccountType().toUpperCase())
                                .createdAt(OffsetDateTime.now())
                                .updatedAt(OffsetDateTime.now())
                                .build();

                        return accountRepository.save(newAccount)
                                .map(savedAccount -> new ApiResponse(
                                        configs.getResponseConfig().getSuccessResponseCode(),
                                        configs.getResponseConfig().getSuccessResponseMessage(),
                                        configs.getResponseConfig().getSuccessCustomerMessage(),
                                        request.getRefId(),
                                        newAccount
                                ));
                    }
                })
                .onErrorResume(error -> {
                    ApiResponse response = new ApiResponse(
                            configs.getResponseConfig().getErrorResponseCode(),
                            configs.getResponseConfig().getErrorResponseMessage(),
                            configs.getResponseConfig().getErrorCustomerMessage(),
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess("Create Account")
                            .setRequest(request)
                            .setResponse(response)
                            .setResponseMsg(error.getMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(response);
                })
                .doOnNext(apiResponse -> logging.setLogLevel("info")
                        .setTransactionID(request.getRefId())
                        .setProcess("Create Account")
                        .setRequest(request)
                        .setResponse(apiResponse)
                        .setResponseMsg(apiResponse.getResponseMessage())
                        .setProcessDuration(System.currentTimeMillis() - startTime)
                        .write());
    }

    @Override
    public Mono<ApiResponse> updateAccount(UpdateAccountRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();

        // Retrieve the account by accountId
        return accountRepository.findById(request.getAccountId())
                .flatMap(account -> {
                    // Update the status if present in the request
                    account.setStatus(request.getStatus());
                    account.setUpdatedAt(OffsetDateTime.now());

                    // Save the updated account
                    return accountRepository.save(account)
                            .map(updatedAccount -> {
                                ApiResponse apiResponse = new ApiResponse(
                                        configs.getResponseConfig().getSuccessResponseCode(),
                                        configs.getResponseConfig().getSuccessResponseMessage(),
                                        configs.getResponseConfig().getSuccessCustomerMessage(),
                                        request.getRefId(),
                                        updatedAccount
                                );

                                logging.setLogLevel("info")
                                        .setTransactionID(request.getRefId())
                                        .setProcess("Update Account")
                                        .setRequest(request)
                                        .setResponse(apiResponse)
                                        .setResponseMsg(apiResponse.getResponseMessage())
                                        .setProcessDuration(System.currentTimeMillis() - startTime)
                                        .write();

                                return apiResponse;
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    ApiResponse apiResponse = new ApiResponse(
                            configs.getResponseConfig().getFailedResponseCode(),
                            "Account not found.",
                            "The account with the provided ID does not exist.",
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess("Update Account")
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(apiResponse.getResponseMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                }))
                .onErrorResume(error -> {
                    ApiResponse apiResponse = new ApiResponse(
                            configs.getResponseConfig().getErrorResponseCode(),
                            configs.getResponseConfig().getErrorResponseMessage(),
                            configs.getResponseConfig().getErrorCustomerMessage(),
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess("Update Account")
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(error.getMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                });
    }

    @Override
    public Mono<ApiResponse> getAccounts(GetAccountsRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();
        String processName = "Get Accounts";

        return accountRepository.findByProfileId(request.getProfileId())
                .collectList() // Collect all accounts for the profile into a list
                .flatMap(accounts -> {
                    if (accounts.isEmpty()) {
                        ApiResponse apiResponse = new ApiResponse(
                                configs.getResponseConfig().getFailedResponseCode(),
                                configs.getResponseConfig().getAccountsNotFoundResponseMessage(),
                                configs.getResponseConfig().getAccountsNotFoundCustomerMessage(),
                                request.getRefId(),
                                null
                        );

                        logging.setLogLevel("info")
                                .setTransactionID(request.getRefId())
                                .setProcess(processName)
                                .setRequest(request)
                                .setResponse(apiResponse)
                                .setResponseMsg(apiResponse.getResponseMessage())
                                .setProcessDuration(System.currentTimeMillis() - startTime)
                                .write();

                        return Mono.just(apiResponse);
                    }

                    ApiResponse apiResponse = new ApiResponse(
                            configs.getResponseConfig().getSuccessResponseCode(),
                            configs.getResponseConfig().getSuccessResponseMessage(),
                            configs.getResponseConfig().getSuccessCustomerMessage(),
                            request.getRefId(),
                            accounts
                    );

                    logging.setLogLevel("info")
                            .setTransactionID(request.getRefId())
                            .setProcess(processName)
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(apiResponse.getResponseMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                })
                .onErrorResume(error -> {
                    ApiResponse apiResponse = new ApiResponse(
                            configs.getResponseConfig().getErrorResponseCode(),
                            configs.getResponseConfig().getErrorResponseMessage(),
                            configs.getResponseConfig().getErrorCustomerMessage(),
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess(processName)
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(error.getMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                });
    }


}
