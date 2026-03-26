package com.bicosteve.api_gateway.dto.requests;


import com.bicosteve.api_gateway.validation.PasswordMatches;
import com.bicosteve.api_gateway.validation.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class RegisterRequest {
    @NotBlank(message = "Phone number is required")
    @Size(max = 10, message = "Phone number must be exactly 10 characters")
    @ValidPhoneNumber
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 1, message = "Must be greater than 1 character")
    @JsonProperty("password")
    private String password;

    @NotBlank(message = "Password is required")
    @Size(min = 1, message = "Must be greater than 1 character")
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
