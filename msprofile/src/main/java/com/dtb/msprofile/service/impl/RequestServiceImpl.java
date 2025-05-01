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
import com.dtb.msprofile.dto.response.ProfileResponse;
import com.dtb.msprofile.service.RequestService;
import com.dtb.msprofile.util.cryptography.Cryptography;
import com.dtb.msprofile.util.logging.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final Configs configs;
    private final Cryptography cryptography;

    private final Logging logging = new Logging();


    @Override
    public Mono<ApiResponse> createProfile(CreateProfileRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();

        return profileRepository.findByEmail(request.getEmail())
                .flatMap(existingProfile -> {
                    ApiResponse apiResponse = new ApiResponse(
                            configs.getResponseConfig().getFailedResponseCode(),
                            configs.getResponseConfig().getCustomerExistsResponseMessage(),
                            configs.getResponseConfig().getCustomerExistsCustomerMessage(),
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("info")
                            .setTransactionID(request.getRefId())
                            .setProcess("Create Profile")
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(apiResponse.getResponseMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    String decryptedPassword = cryptography.decrypt(request.getPassword(), request.getRefId());
                    String hashedPassword = BCrypt.hashpw(decryptedPassword, BCrypt.gensalt());

                    Profile profile = Profile.builder()
                            .email(request.getEmail())
                            .username(request.getUsername())
                            .passwordHash(hashedPassword)
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .role(request.getRole())
                            .createdAt(OffsetDateTime.now())
                            .updatedAt(OffsetDateTime.now())
                            .build();

                    return profileRepository.save(profile)
                            .flatMap(savedProfile -> {
                                Account walletAccount = Account.builder()
                                        .profileId(profile.getId()) // use ID after save
                                        .balance(BigDecimal.valueOf(10000.00))
                                        .status("ACTIVE")
                                        .accountType("WALLET")
                                        .createdAt(OffsetDateTime.now())
                                        .updatedAt(OffsetDateTime.now())
                                        .build();

                                return accountRepository.save(walletAccount)
                                        .thenReturn(new ApiResponse(
                                                configs.getResponseConfig().getSuccessResponseCode(),
                                                configs.getResponseConfig().getSuccessResponseMessage(),
                                                configs.getResponseConfig().getSuccessCustomerMessage(),
                                                request.getRefId(),
                                                walletAccount
                                        ));
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
                                        .setProcess("Create Profile")
                                        .setRequest(request)
                                        .setResponse(apiResponse)
                                        .setResponseMsg(error.getMessage())
                                        .setProcessDuration(System.currentTimeMillis() - startTime)
                                        .write();

                                return Mono.just(apiResponse);
                            });
                }))
                .doOnNext(apiResponse -> logging.setLogLevel("info")
                        .setTransactionID(request.getRefId())
                        .setProcess("Create Profile")
                        .setRequest(request)
                        .setResponse(apiResponse)
                        .setResponseMsg(apiResponse.getResponseMessage())
                        .setProcessDuration(System.currentTimeMillis() - startTime)
                        .write());
    }

    @Override
    public Mono<ApiResponse> updateProfile(UpdateProfileRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();
        String decryptedPassword = cryptography.decrypt(request.getPassword(), request.getRefId());

        return profileRepository.findByEmail(request.getEmail())
                .flatMap(profile -> {
                    // Check password match
                    if (!BCrypt.checkpw(decryptedPassword, profile.getPasswordHash())) {
                        ApiResponse apiResponse = new ApiResponse(
                                configs.getResponseConfig().getFailedResponseCode(),
                                configs.getResponseConfig().getInvalidCredentialsResponseMessage(),
                                configs.getResponseConfig().getInvalidCredentialsCustomerMessage(),
                                request.getRefId(),
                                null
                        );

                        logging.setLogLevel("error")
                                .setTransactionID(request.getRefId())
                                .setProcess("Update Profile")
                                .setRequest(request)
                                .setResponse(apiResponse)
                                .setResponseMsg(apiResponse.getResponseMessage())
                                .setProcessDuration(System.currentTimeMillis() - startTime)
                                .write();

                        return Mono.just(apiResponse);
                    }

                    // Update fields if present
                    if (request.getNewPassword() != null) {
                        String decryptedNewPassword = cryptography.decrypt(request.getNewPassword(), request.getRefId());
                        profile.setPasswordHash(BCrypt.hashpw(decryptedNewPassword, BCrypt.gensalt()));
                    }

                    if (request.getUsername() != null) profile.setUsername(request.getUsername());
                    if (request.getFirstName() != null) profile.setFirstName(request.getFirstName());
                    if (request.getLastName() != null) profile.setLastName(request.getLastName());
                    if (request.getRole() != null) profile.setRole(request.getRole());



                    profile.setUpdatedAt(OffsetDateTime.now());

                    return profileRepository.save(profile)
                            .map(updated -> {
                                ApiResponse apiResponse = new ApiResponse(
                                        configs.getResponseConfig().getSuccessResponseCode(),
                                        configs.getResponseConfig().getSuccessResponseMessage(),
                                        configs.getResponseConfig().getSuccessCustomerMessage(),
                                        request.getRefId(),
                                        new ProfileResponse(
                                                profile.getId(),
                                                profile.getEmail(),
                                                profile.getUsername(),
                                                profile.getFirstName(),
                                                profile.getLastName(),
                                                profile.getRole(),
                                                profile.getCreatedAt(),
                                                profile.getUpdatedAt()
                                        )
                                );

                                logging.setLogLevel("info")
                                        .setTransactionID(request.getRefId())
                                        .setProcess("Update Profile")
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
                            configs.getResponseConfig().getCustomerNotFoundResponseMessage(),
                            configs.getResponseConfig().getCustomerNotFoundCustomerMessage(),
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess("Update Profile")
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
                            .setProcess("Update Profile")
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(error.getMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                });
    }
    @Override
    public Mono<ApiResponse> login(LoginRequest request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();
        String decryptedPassword = cryptography.decrypt(request.getPassword(), request.getRefId());

        return profileRepository.findByEmail(request.getEmail())
                .flatMap(profile -> {

                    if (!BCrypt.checkpw(decryptedPassword, profile.getPasswordHash())) {
                        ApiResponse apiResponse = new ApiResponse(
                                configs.getResponseConfig().getFailedResponseCode(),
                                configs.getResponseConfig().getInvalidCredentialsResponseMessage(),
                                configs.getResponseConfig().getInvalidCredentialsCustomerMessage(),
                                request.getRefId(),
                                null
                        );

                        logging.setLogLevel("error")
                                .setTransactionID(request.getRefId())
                                .setProcess("Login")
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
                            new ProfileResponse(
                                    profile.getId(),
                                    profile.getEmail(),
                                    profile.getUsername(),
                                    profile.getFirstName(),
                                    profile.getLastName(),
                                    profile.getRole(),
                                    profile.getCreatedAt(),
                                    profile.getUpdatedAt()
                            )
                    );

                    logging.setLogLevel("info")
                            .setTransactionID(request.getRefId())
                            .setProcess("Login")
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(apiResponse.getResponseMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);

                }).switchIfEmpty(Mono.defer(() -> {
                    ApiResponse apiResponse = new ApiResponse(
                            configs.getResponseConfig().getFailedResponseCode(),
                            configs.getResponseConfig().getCustomerNotFoundResponseMessage(),
                            configs.getResponseConfig().getCustomerNotFoundCustomerMessage(),
                            request.getRefId(),
                            null
                    );

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess("Login")
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
                            .setProcess("Login")
                            .setRequest(request)
                            .setResponse(apiResponse)
                            .setResponseMsg(error.getMessage())
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(apiResponse);
                });
    }



}
