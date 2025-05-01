package com.dtb.mspayment.dto.request;

import com.dtb.mspayment.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalDeposit {

    @NotBlank(message = "RefId is mandatory")
    @JsonProperty
    private String refId;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @JsonProperty
    private String email;

    @NotBlank(message = "Password is mandatory")
    @JsonProperty
    private String password;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @JsonProperty
    private BigDecimal amount;

    @NotBlank(message = "Transaction type is mandatory")
    @ListValue(allowedValues = {"WITHDRAW", "DEPOSIT"}, message = "Invalid type. Allowed values are: {allowedValues}")
    @JsonProperty
    private String type;

    @NotNull(message = "Account ID is required")
    @JsonProperty
    private UUID accountId;
}
