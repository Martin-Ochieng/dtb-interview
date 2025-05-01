package com.dtb.mspayment.dto.request;


import com.dtb.mspayment.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    @NotBlank(message = "Refid is mandatory")
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
    @JsonProperty
    private BigDecimal amount;

    @NotBlank(message = "Account ID is mandatory")
    @JsonProperty
    private String accountId;

    @NotBlank(message = "Receiver email is mandatory")
    @Email(message = "Receiver email must be valid")
    @JsonProperty
    private String receiver;
}
