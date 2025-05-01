package com.dtb.msaccount.service.impl;

import com.dtb.msaccount.config.Configs;
import com.dtb.msaccount.data.entity.Account;
import com.dtb.msaccount.data.repo.AccountRepository;
import com.dtb.msaccount.dto.request.CreateAccountRequest;
import com.dtb.msaccount.dto.request.GetAccountsRequest;
import com.dtb.msaccount.dto.request.UpdateAccountRequest;
import com.dtb.msaccount.dto.response.ApiResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.OutputCaptureExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class RequestServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private Configs configs;


    @InjectMocks
    private RequestServiceImpl requestService;

    private Configs mockConfigs;

    private CreateAccountRequest createAccountRequest;

    private UpdateAccountRequest updateAccountRequest;

    private GetAccountsRequest getAccountsRequest;

    private Map<String,String > headers;

    private Account account;




    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockConfigs = new Configs(
                new Configs.ResponseConfig(
                        "0", // successResponseCode
                        "Request successful", // successResponseMessage
                        "Request Executed Successfully", // successCustomerMessage
                        "1", // failedResponseCode
                        "Request failed", // failedResponseMessage
                        "Request Execution Failed", // failedCustomerMessage
                        "2", // errorResponseCode
                        "An error occurred while processing your request", // errorResponseMessage
                        "An error occurred while processing your request", // errorCustomerMessage
                        "Account already exists", // customerExistsResponseMessage
                        "Account already exists", // customerExistsCustomerMessage
                        "Invalid credentials", // invalidCredentialsResponseMessage
                        "Invalid Email or Password. PLease check and try again", // invalidCredentialsCustomerMessage
                        "Account not found", // customerNotFoundResponseMessage
                        "Account with the given email not found" // customerNotFoundCustomerMessage
                )
        );



        createAccountRequest = new CreateAccountRequest(
                "{{$guid}}",
                UUID.randomUUID(),
                "WALLET"
        );

        updateAccountRequest = new UpdateAccountRequest(
                "{{$guid}}",
                UUID.randomUUID(),
                "CLOSED"
        );

        getAccountsRequest = new GetAccountsRequest(
                "{{$guid}}",
                UUID.randomUUID()
        );

        headers = new HashMap<>();

        account = new Account(
                UUID.randomUUID(), // id
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), // profileId
                new BigDecimal("1000.00"), // balance
                "ACTIVE", // status
                "WALLET", // accountType
                OffsetDateTime.now().minusDays(1), // createdAt
                OffsetDateTime.now() // updatedAt
        );



        requestService = new RequestServiceImpl(accountRepository,configs);







        // Initialize any necessary mocks or test data here
    }


    @Test
    void createAccount() {

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findByProfileId(createAccountRequest.getProfileId()))
                .thenReturn(Flux.empty());

        when(accountRepository.save(any())).thenReturn(Mono.just(new Account()));

        ApiResponse apiResponse= requestService.createAccount(createAccountRequest, headers).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void createAccountAlreadyExists() {

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findByProfileId(createAccountRequest.getProfileId()))
                .thenReturn(Flux.just(account));


        ApiResponse apiResponse= requestService.createAccount(createAccountRequest, headers).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }



    @Test
    void createAccountError() {

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findByProfileId(createAccountRequest.getProfileId()))
                .thenReturn(Flux.error(new IllegalArgumentException("Error occurred")));



        ApiResponse apiResponse= requestService.createAccount(createAccountRequest, new HashMap<>()).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getErrorResponseCode(), apiResponse.getResponseCode());


    }

    @Test
    void updateAccountAccountNotFound() {

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findById(updateAccountRequest.getAccountId()))
                .thenReturn(Mono.empty());


        ApiResponse apiResponse= requestService.updateAccount(updateAccountRequest, new HashMap<>()).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void updateAccount() {

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findById(updateAccountRequest.getAccountId()))
                .thenReturn(Mono.just(account));


        when(accountRepository.save(any())).thenReturn(Mono.just(new Account()));
        ApiResponse apiResponse= requestService.updateAccount(updateAccountRequest, new HashMap<>()).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void updateAccountError() {

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findById(updateAccountRequest.getAccountId()))
                .thenReturn(Mono.error(new IllegalArgumentException("Error occurred")));



        ApiResponse apiResponse= requestService.updateAccount(updateAccountRequest, new HashMap<>()).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getErrorResponseCode(), apiResponse.getResponseCode());


    }

    @Test
    void getAccounts() {


        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findByProfileId(getAccountsRequest.getProfileId()))
                .thenReturn(Flux.just(account));


        ApiResponse apiResponse= requestService.getAccounts(getAccountsRequest, new HashMap<>()).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void getAccountsAccountsNotFound() {


        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findByProfileId(getAccountsRequest.getProfileId()))
                .thenReturn(Flux.empty());


        ApiResponse apiResponse= requestService.getAccounts(getAccountsRequest, headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }

    @Test
    void getAccountsError() {


        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());

        when(accountRepository.findByProfileId(getAccountsRequest.getProfileId()))
                .thenReturn(Flux.error(new IllegalArgumentException("Error occurred")));


        ApiResponse apiResponse= requestService.getAccounts(getAccountsRequest, new HashMap<>()).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getErrorResponseCode(), apiResponse.getResponseCode());


    }
}