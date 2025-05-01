package com.dtb.msprofile.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Refid is mandatory")
    @NotNull(message = "Refid cannot be null")
    @JsonProperty
    private String refId;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @JsonProperty
    private String email;

    @NotBlank(message = "Password is mandatory")
    @NotNull(message = "Password cannot be null")
    @JsonProperty
    private String password;


}
