package com.dtb.msaccount.dto.request;


import com.dtb.msaccount.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "Refid is mandatory")
    @NotNull(message = "Refid cannot be null")
    @JsonProperty
    private String refId;

    @NotNull(message = "Profile ID is required")
    @JsonProperty
    private UUID profileId;


    @NotBlank(message = "Account type is mandatory")
    @NotNull(message = "Account type cannot be null")
    @ListValue(allowedValues = {"WALLET", "SAVING"}, message = "Account type must be WALLET or SAVING")
    @JsonProperty
    private String accountType;
}
