package com.dtb.mspayment.service.impl;

import com.dtb.mspayment.config.Configs;
import com.dtb.mspayment.data.entity.Transaction;
import com.dtb.mspayment.data.repo.AccountRepository;
import com.dtb.mspayment.data.repo.ProfileRepository;
import com.dtb.mspayment.data.repo.TransactionRepository;
import com.dtb.mspayment.dto.request.WithdrawalDeposit;
import com.dtb.mspayment.dto.request.Transfer;
import com.dtb.mspayment.dto.response.ApiResponse;
import com.dtb.mspayment.producer.RabbitMqProducer;
import com.dtb.mspayment.service.RequestService;
import com.dtb.mspayment.util.cryptography.Cryptography;
import com.dtb.mspayment.util.logging.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final ProfileRepository profileRepository;
    private final AccountRepository accountRepository;
    private final Configs configs;
    private final Cryptography cryptography;
    private final RabbitMqProducer rabbitMqProducer;
    private final TransactionRepository transactionRepository;

    private final Logging logging = new Logging();


    @Override
    public Mono<ApiResponse> withdrawalDeposit(WithdrawalDeposit request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();

        return profileRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(configs.getResponseConfig().getCustomerNotFoundResponseMessage())))
                .flatMap(profile -> {
                    String decryptedPassword = cryptography.decrypt(request.getPassword(), request.getRefId());

                    if (!BCrypt.checkpw(decryptedPassword, profile.getPasswordHash())) {
                        return Mono.error(new IllegalArgumentException(configs.getResponseConfig().getInvalidCredentialsResponseMessage()));
                    }

                    return accountRepository.findById(request.getAccountId())

                            .flatMap(account -> {
                                BigDecimal amount = request.getAmount();
                                String type = request.getType();

                                if ("WITHDRAW".equalsIgnoreCase(type)) {
                                    if (account.getBalance().compareTo(amount) < 0) {
                                        return Mono.just(new ApiResponse(
                                                configs.getResponseConfig().getFailedResponseCode(),
                                                "Insufficient funds",
                                                "You do not have enough balance for this withdrawal.",
                                                request.getRefId(),
                                                null
                                        ));
                                    }
                                    account.setBalance(account.getBalance().subtract(amount));
                                } else  {
                                    account.setBalance(account.getBalance().add(amount));
                                }

                                Transaction transaction = Transaction.builder()
                                        .accountId(account.getId())
                                        .type("TRANSFER")
                                        .amount(request.getAmount())
                                        .relatedAccount(request.getAccountId()) // Ensure request has this field
                                        .createdAt(OffsetDateTime.now())
                                        .build();

                                return transactionRepository.save(transaction)
                                        .map(savedTransaction -> {

                                            rabbitMqProducer.sendMessage("Transaction :"+ transaction.getId()+" completed successfully",
                                                    request.getEmail());

                                                       return new ApiResponse(
                                                configs.getResponseConfig().getSuccessResponseCode(),
                                                configs.getResponseConfig().getSuccessResponseMessage(),
                                                configs.getResponseConfig().getSuccessCustomerMessage(),
                                                request.getRefId(),
                                                transaction
                                        );
                                                }

                                        );
                            }).switchIfEmpty(Mono.defer(()-> Mono.error(new IllegalArgumentException("Account not found"))));
                })
                .onErrorResume(error -> {
                    String msg = error.getMessage();
                    String code = configs.getResponseConfig().getFailedResponseCode();
                    String userMsg = error.getMessage();


                    ApiResponse errorResponse = new ApiResponse(code, msg, userMsg, request.getRefId(), null);

                    logging.setLogLevel("error")
                            .setTransactionID(request.getRefId())
                            .setProcess(request.getType())
                            .setRequest(request)
                            .setResponse(errorResponse)
                            .setResponseMsg(msg)
                            .setProcessDuration(System.currentTimeMillis() - startTime)
                            .write();

                    return Mono.just(errorResponse);
                })
                .doOnNext(response -> logging.setLogLevel("info")
                        .setTransactionID(request.getRefId())
                        .setProcess(request.getType())
                        .setRequest(request)
                        .setResponse(response)
                        .setResponseMsg(response.getResponseMessage())
                        .setProcessDuration(System.currentTimeMillis() - startTime)
                        .write());
    }


    @Override
    public Mono<ApiResponse> transfer(Transfer request, Map<String, String> headers) {
        long startTime = System.currentTimeMillis();

        return profileRepository.findByEmail(request.getEmail())
                .flatMap(profile -> {
                    String decryptedPassword = cryptography.decrypt(request.getPassword(), request.getRefId());

                    if (!BCrypt.checkpw(decryptedPassword, profile.getPasswordHash())) {
                        return Mono.just(new ApiResponse(
                                configs.getResponseConfig().getFailedResponseCode(),
                                "Invalid credentials",
                                "Authentication failed",
                                request.getRefId(),
                                null
                        ));
                    }

                    return accountRepository.findById(UUID.fromString(request.getAccountId()))
                            .flatMap(payerAccount -> {
                                if (payerAccount.getBalance().compareTo(request.getAmount()) < 0) {
                                    return Mono.just(new ApiResponse(
                                            configs.getResponseConfig().getFailedResponseCode(),
                                            "Insufficient balance",
                                            "You don't have enough funds",
                                            request.getRefId(),
                                            null
                                    ));
                                }

                                return profileRepository.findByEmail(request.getReceiver())
                                        .flatMap(receiverProfile ->
                                                accountRepository.findByProfileId(receiverProfile.getId())
                                                        .single()
                                                        .flatMap(receiverAccount -> {
                                                            // Update balances
                                                            payerAccount.setBalance(payerAccount.getBalance().subtract(request.getAmount()));
                                                            receiverAccount.setBalance(receiverAccount.getBalance().add(request.getAmount()));

                                                            Transaction debitTransaction = Transaction.builder()
                                                                    .accountId(payerAccount.getId())
                                                                    .type("DEBIT")
                                                                    .amount(request.getAmount())
                                                                    .relatedAccount(receiverAccount.getId())
                                                                    .createdAt(OffsetDateTime.now())
                                                                    .build();

                                                            Transaction creditTransaction = Transaction.builder()
                                                                    .accountId(receiverAccount.getId())
                                                                    .type("CREDIT")
                                                                    .amount(request.getAmount())
                                                                    .relatedAccount(payerAccount.getId())
                                                                    .createdAt(OffsetDateTime.now())
                                                                    .build();

                                                            return transactionRepository.save(debitTransaction)
                                                                    .then(transactionRepository.save(creditTransaction))
                                                                    .then(Mono.fromRunnable(() ->
                                                                            rabbitMqProducer.sendMessage(
                                                                                    "Debit " + debitTransaction.getId() + " Transfer successful to " + receiverProfile.getEmail(),
                                                                                    request.getEmail()))
                                                                    )
                                                                    .then(Mono.fromRunnable(() ->
                                                                            rabbitMqProducer.sendMessage(
                                                                                    "Credit " + creditTransaction.getId() + " Transfer successful from " + request.getEmail(),
                                                                                    receiverProfile.getEmail()))
                                                                    )
                                                                    .thenReturn(new ApiResponse(
                                                                            configs.getResponseConfig().getSuccessResponseCode(),
                                                                            "Debit " + debitTransaction.getId() + " Transfer successful to " + receiverProfile.getEmail(),
                                                                            "Funds transferred successfully",
                                                                            request.getRefId(),
                                                                            null
                                                                    ));
                                                        })
                                        );
                            }).onErrorResume(error -> buildErrorResponse(request, error.getMessage(), startTime));
                })
                .onErrorResume(error -> buildErrorResponse(request, error.getMessage(), startTime))
                .doOnNext(response -> logging.setLogLevel("info")
                        .setTransactionID(request.getRefId())
                        .setProcess("Transfer Funds")
                        .setRequest(request)
                        .setResponse(response)
                        .setResponseMsg(response.getResponseMessage())
                        .setProcessDuration(System.currentTimeMillis() - startTime)
                        .write()
                );
    }


    private Mono<ApiResponse> buildErrorResponse(Transfer request, String errorMsg, long startTime) {
        ApiResponse errorResponse = new ApiResponse(
                configs.getResponseConfig().getFailedResponseCode(),
                errorMsg,
                errorMsg,
                request.getRefId(),
                null
        );

        logging.setLogLevel("error")
                .setTransactionID(request.getRefId())
                .setProcess("Transfer Funds")
                .setRequest(request)
                .setResponse(errorResponse)
                .setResponseMsg(errorMsg)
                .setProcessDuration(System.currentTimeMillis() - startTime)
                .write();

        return Mono.just(errorResponse);
    }





}
