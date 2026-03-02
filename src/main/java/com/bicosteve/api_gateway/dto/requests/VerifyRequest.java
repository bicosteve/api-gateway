package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyRequest {
    @NotBlank(message = "Phone number is required")
    @Size(max = 10, message = "Phone number must be exactly 10 characters")
    @ValidPhoneNumber
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Verification code is required")
    @Size(min = 4, max = 6, message = "Must be 4 or 6 characters")
    @JsonProperty("verification_code")
    private String verificationCode;
}
