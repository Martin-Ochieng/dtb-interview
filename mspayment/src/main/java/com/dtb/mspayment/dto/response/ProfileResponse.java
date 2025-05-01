package com.dtb.mspayment.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    @JsonProperty
    private UUID profileId;

    @JsonProperty
    private String username;

    @JsonProperty
    private String email;

    @JsonProperty
    private String firstName;


    @JsonProperty
    private String lastName;

    @JsonProperty
    private String role;

    @JsonProperty
    private OffsetDateTime createdAt;

    @JsonProperty
    private OffsetDateTime updatedAt;
}
