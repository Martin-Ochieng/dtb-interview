package com.dtb.msprofile.dto.request;


import com.dtb.msprofile.util.inputvalidation.validstringslist.ListValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {

    @NotBlank(message = "Refid is mandatory")
    @NotNull(message = "Refid cannot be null")
    @JsonProperty
    private String refId;

    @NotBlank(message = "Username is mandatory")
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @JsonProperty
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be null")
    @JsonProperty
    private String email;

    @NotBlank(message = "Password is mandatory")
    @NotNull(message = "Password cannot be null")
    @JsonProperty
    private String password;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 100, message = "First name can be at most 100 characters")
    @NotNull(message = "First name cannot be null")
    @JsonProperty
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 100, message = "Last name can be at most 100 characters")
    @NotNull(message = "Last name cannot be null")
    @JsonProperty
    private String lastName;

    @JsonProperty
    @ListValue(allowedValues = {"CUSTOMER", "ADMIN"}, message = "Invalid Role. Allowed values are: {allowedValues}")
    private String role;
}
