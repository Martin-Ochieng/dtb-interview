package com.dtb.msaccount.dto.request;


import com.dtb.msaccount.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {

    @NotBlank(message = "Refid is mandatory")
    @NotNull(message = "Refid cannot be null")
    @JsonProperty
    private String refId;


    @NotNull(message = "Account ID is required")
    @JsonProperty
    private UUID accountId;

    @JsonProperty
    @NotNull(message = "Status is required")
    @NotBlank(message = "Status cannot be blank")
    @ListValue(allowedValues = {"ACTIVE", "CLOSED"}, message = "Invalid Role. Allowed values are: {allowedValues}")
    private String status;
}
