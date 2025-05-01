package com.dtb.msprofile.service.impl;

import com.dtb.msprofile.config.Configs;
import com.dtb.msprofile.data.entity.Account;
import com.dtb.msprofile.data.entity.Profile;
import com.dtb.msprofile.data.repo.AccountRepository;
import com.dtb.msprofile.data.repo.ProfileRepository;
import com.dtb.msprofile.dto.request.CreateProfileRequest;
import com.dtb.msprofile.dto.request.LoginRequest;
import com.dtb.msprofile.dto.request.UpdateProfileRequest;
import com.dtb.msprofile.dto.response.ApiResponse;
import com.dtb.msprofile.util.cryptography.Cryptography;
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
import reactor.core.publisher.Mono;

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

    @InjectMocks
    private RequestServiceImpl requestService;

    private Configs mockConfigs;

    private CreateProfileRequest createProfileRequest;

    private UpdateProfileRequest updateProfileRequest;

    private LoginRequest loginRequest;

    private Map<String,String > headers;

    private Profile profile;


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
                        "Invalid credentials", // invalidCredentialsResponseMessage
                        "Invalid Email or Password. PLease check and try again", // invalidCredentialsCustomerMessage
                        "Account not found", // customerNotFoundResponseMessage
                        "Account with the given email not found" // customerNotFoundCustomerMessage
                )
        );


        createProfileRequest = new CreateProfileRequest(
                "TXN123456789",              // refId
                "john_doe",                  // username
                "john.doe@example.com",      // email
                "EncryptedBase64PasswordHere", // password (should be base64 encoded encrypted password)
                "John",                      // firstName
                "Doe",                       // lastName
                "CUSTOMER"                   // role (can be "CUSTOMER" or "ADMIN")
        );

        updateProfileRequest = new UpdateProfileRequest(
                "TXN123456789",                  // refId
                "john_doe",                      // username (optional)
                "john.doe@example.com",          // email
                "EncryptedBase64PasswordHere",   // password (should be base64 encoded encrypted password)
                "NewEncryptedBase64PasswordHere", // newPassword (optional, only if updating password)
                "John",                          // firstName (optional)
                "Doe",                           // lastName (optional)
                "CUSTOMER"                       // role (can be "CUSTOMER" or "ADMIN")
        );

        loginRequest = new LoginRequest(
                "TXN123456789",                  // refId
                "john.doe@example.com",          // email
                "EncryptedBase64PasswordHere"   // password (should be base64 encoded encrypted password)

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

        requestService = new RequestServiceImpl(profileRepository,accountRepository,configs,cryptography);







        // Initialize any necessary mocks or test data here
    }

    @Test
    void createProfile() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.empty());
        when(cryptography.decrypt(any(),any())).thenReturn("EncryptedPassword");
        when(profileRepository.save(any())).thenReturn(Mono.just(new Profile()));
        when(accountRepository.save(any())).thenReturn(Mono.just(new Account()));

        ApiResponse apiResponse= requestService.createProfile(createProfileRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());


    }

    @Test
    void createProfileCustomerExists() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(new Profile()));

        ApiResponse apiResponse= requestService.createProfile(createProfileRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void createProfileError() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.empty());
        when(cryptography.decrypt(any(),any())).thenReturn("EncryptedPassword");
        when(profileRepository.save(any())).thenReturn(Mono.error(new IllegalArgumentException("An Error Occurred")));

        ApiResponse apiResponse= requestService.createProfile(createProfileRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getErrorResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void updateProfile() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword");
        when(profileRepository.save(any())).thenReturn(Mono.just(new Profile()));

        ApiResponse apiResponse= requestService.updateProfile(updateProfileRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());


    }

    @Test
    void updateProfileAllNullError() {

        updateProfileRequest.setNewPassword(null);
        updateProfileRequest.setUsername(null);
        updateProfileRequest.setFirstName(null);
        updateProfileRequest.setLastName(null);
        updateProfileRequest.setRole(null);

        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword");
        when(profileRepository.save(any())).thenReturn(Mono.error(new IllegalArgumentException("An Error Occurred")));

        ApiResponse apiResponse= requestService.updateProfile(updateProfileRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getErrorResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void updateProfileWrongPassword() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword2");

        ApiResponse apiResponse= requestService.updateProfile(updateProfileRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void updateProfileCustomerNotFound() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.empty());

        ApiResponse apiResponse= requestService.updateProfile(updateProfileRequest,headers).block();

        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void login() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword");


        ApiResponse apiResponse= requestService.login(loginRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getSuccessResponseCode(), apiResponse.getResponseCode());


    }


    @Test
    void loginInvalidPassword() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.just(profile));
        when(cryptography.decrypt(any(),any())).thenReturn("decryptedPassword2");


        ApiResponse apiResponse= requestService.login(loginRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }

    @Test
    void loginCustomerNotFound() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.empty());


        ApiResponse apiResponse= requestService.login(loginRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getFailedResponseCode(), apiResponse.getResponseCode());


    }



    @Test
    void loginCustomerError() {
        when(configs.getResponseConfig()).thenReturn(mockConfigs.getResponseConfig());
        when(profileRepository.findByEmail(any())).thenReturn(Mono.error(new IllegalArgumentException("An Error Occurred")));


        ApiResponse apiResponse= requestService.login(loginRequest,headers).block();


        Assertions.assertNotNull(apiResponse);
        Assertions.assertEquals(mockConfigs.getResponseConfig().getErrorResponseCode(), apiResponse.getResponseCode());


    }
}