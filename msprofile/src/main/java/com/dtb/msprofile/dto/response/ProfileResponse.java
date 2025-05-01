package com.dtb.msprofile.dto.response;


import com.dtb.msprofile.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

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
