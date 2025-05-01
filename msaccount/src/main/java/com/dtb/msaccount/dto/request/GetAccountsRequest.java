package com.dtb.msaccount.dto.request;


import com.dtb.msaccount.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountsRequest {

    @NotBlank(message = "Refid is mandatory")
    @NotNull(message = "Refid cannot be null")
    @JsonProperty
    private String refId;

    @NotNull(message = "Profile ID is required")
    @JsonProperty
    private UUID profileId;



}
