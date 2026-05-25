package com.bicosteve.api_gateway.dto.requests;


import com.bicosteve.api_gateway.validation.PasswordMatches;
import com.bicosteve.api_gateway.validation.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Schema(example = "254701234567")
    @NotBlank(message = "Phone number is required")
    @Size(max = 12, message = "Phone number must be exactly 12 characters")
    @ValidPhoneNumber
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Schema(example = "test@example.com")
    @NotBlank(message = "Email is required")
    @JsonProperty("email")
    private String email;

    @Schema(example = "pass1234")
    @NotBlank(message = "Password is required")
    @Size(min = 1, message = "Must be greater than 1 character")
    @JsonProperty("password")
    private String password;

    @Schema(example = "pass1234")
    @NotBlank(message = "Password is required")
    @Size(min = 1, message = "Must be greater than 1 character")
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
