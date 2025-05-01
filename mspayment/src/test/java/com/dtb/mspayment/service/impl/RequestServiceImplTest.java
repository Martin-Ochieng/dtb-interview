package com.dtb.mspayment.service.impl;

import com.dtb.mspayment.config.Configs;
import com.dtb.mspayment.data.entity.Account;
import com.dtb.mspayment.data.entity.Profile;
import com.dtb.mspayment.data.entity.Transaction;
import com.dtb.mspayment.data.repo.AccountRepository;
import com.dtb.mspayment.data.repo.ProfileRepository;
import com.dtb.mspayment.data.repo.TransactionRepository;
import com.dtb.mspayment.dto.request.WithdrawalDeposit;
import com.dtb.mspayment.dto.request.Transfer;
import com.dtb.mspayment.dto.response.ApiResponse;
import com.dtb.mspayment.producer.RabbitMqProducer;
import com.dtb.mspayment.util.cryptography.Cryptography;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
    private ProfileRepository profileRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private Configs configs;
    @Mock
    private Cryptography cryptography;

    @Mock
    private RabbitMqProducer rabbitMqProducer;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private Configs mockConfigs;

    private WithdrawalDeposit withdrawalDeposit;

    private Transfer transfer;


    private Map<String,String > headers;

    private Profile profile;

    private Account account;

    private Account account2;


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
                        "Customer already exists", // customerExistsResponseMessage
                        "Customer already exists with the given Email", // customerExistsCustomerMessage
                        "Customer Not Found", // invalidCredentialsResponseMessage
                        "Customer Not Found", // invalidCredentialsCustomerMessage
                        "Invalid Credentials", // customerNotFoundResponseMessage
                        "Invalid Credentials", // customerNotFoundCustomerMessage
                        "exchage",
                        "routingKey" // customerNotFoundCustomerMessage
                )
        );


        withdrawalDeposit = new WithdrawalDeposit(
                "{{$guid}}",
                "janedoe@example.com",
                "Pass",
                BigDecimal.valueOf(1000),
                "WITHDRAW",
                UUID.randomUUID()
        );

        transfer = new Transfer(
                "UUID",
                "janedoe@example.com",
                "Fffff",
                BigDecimal.valueOf(1000),
                "d5f9c4d0-6af8-4f5b-a8a2-3f4d8be748df",
                "janedoe@example.com"
        );

        account = new Account(
                UUID.randomUUID(), // id
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), // profileId
                new BigDecimal("100000.00"), // balance
                "ACTIVE", // status
                "WALLET", // accountType
                OffsetDateTime.now().minusDays(1), // createdAt
                OffsetDateTime.now() // updatedAt
        );

        account2 = new Account(
                UUID.randomUUID(), // id
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), // profileId
                new BigDecimal("100000.00"), // balance
                "ACTIVE", // status
                "WALLET", // accountType
                OffsetDateTime.now().minusDays(1), // createdAt
                OffsetDateTime.now() // updatedAt
        );



        headers = new HashMap<>();

        profile = new Profile(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), // id
                "john_doe",                                              // username
                "john.doe@example.com",                                  // email
                BCrypt.hashpw("decryptedPassword", BCrypt.gensalt()),                          // passwordHash
                "John",                                                  // firstName
                "Doe",                                                   // lastName
                "CUSTOMER",                                              // role
                OffsetDateTime.parse("2024-04-01T10:15:30+03:00"),       // createdAt
                OffsetDateTime.parse("2025-04-30T10:15:30+03:00")        // updatedAt
        );

        requestService = new RequestServiceImpl(profileRepository,accountRepository,configs,cryptography, rabbitMqProducer, transactionRepository);







        // Initialize any necessary mocks or test data here
    }

    @Test
    void withdrawalDeposit() {
        when(transactionRepository.save(any())).thenReturn(Mono.just(new Transaction()));
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(accountRepository.findById(withdrawalDeposit.getAccountId())).thenReturn(Mono.just(account));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword");



        ApiResponse apiResponse= requestService.withdrawalDeposit(withdrawalDeposit,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());

    }


    @Test
    void withdrawalDepositInsufficient() {

        withdrawalDeposit.setAmount(BigDecimal.valueOf(100));
        account.setBalance(BigDecimal.valueOf(50));
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(accountRepository.findById(withdrawalDeposit.getAccountId())).thenReturn(Mono.just(account));

        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword");

        ApiResponse apiResponse= requestService.withdrawalDeposit(withdrawalDeposit,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());

    }


    @Test
    void withdrawalDepositCustomerNotFound() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.empty());


        ApiResponse apiResponse= requestService.withdrawalDeposit(withdrawalDeposit,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());

    }

    @Test
    void withdrawalDepositWrongPin() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));

        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword22");

        ApiResponse apiResponse= requestService.withdrawalDeposit(withdrawalDeposit,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());

    }


    @Test
    void withdrawalDepositDeposit() {
        withdrawalDeposit.setType("DEPOSIT");
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(accountRepository.findById(withdrawalDeposit.getAccountId())).thenReturn(Mono.just(account));
        when(transactionRepository.save(any())).thenReturn(Mono.just(new Transaction()));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword");

        ApiResponse apiResponse= requestService.withdrawalDeposit(withdrawalDeposit,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());

    }

    @Test
    void transferSuccess() {
        when(transactionRepository.save(any())).thenReturn(Mono.just(new Transaction()));
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(transfer.getEmail())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(), any())).thenReturn("decryptedPassword");
        when(accountRepository.findById(UUID.fromString(transfer.getAccountId()))).thenReturn(Mono.just(account));
        when(profileRepository.findByEmail(transfer.getReceiver())).thenReturn(Mono.just(profile));
        when(accountRepository.findByProfileId(profile.getId())).thenReturn(Flux.just(account2));


        ApiResponse response = requestService.transfer(transfer, headers).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), response.getResponseCode());
        Assertions.assertEquals("Funds transferred successfully", response.getCustomerMessage());
    }

    @Test
    void transferInsufficientBalance() {
        account.setBalance(BigDecimal.valueOf(500)); // Less than requested amount
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(transfer.getEmail())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(), any())).thenReturn("decryptedPassword");
        when(accountRepository.findById(UUID.fromString(transfer.getAccountId()))).thenReturn(Mono.just(account));

        ApiResponse response = requestService.transfer(transfer, headers).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), response.getResponseCode());
        Assertions.assertEquals("You don't have enough funds", response.getCustomerMessage());
    }

    @Test
    void transferInvalidCredentials() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(transfer.getEmail())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(), any())).thenReturn("wrongPassword");

        ApiResponse response = requestService.transfer(transfer, headers).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), response.getResponseCode());
        Assertions.assertEquals("Authentication failed", response.getCustomerMessage());
    }

    @Test
    void transferPayerAccountNotFound() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(transfer.getEmail())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(), any())).thenReturn("decryptedPassword");
        when(accountRepository.findById(UUID.fromString(transfer.getAccountId()))).thenReturn(Mono.error(new RuntimeException("Payer account not found")));

        ApiResponse response = requestService.transfer(transfer, headers).block();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), response.getResponseCode());
        Assertions.assertEquals("Payer account not found", response.getResponseMessage());
    }


}