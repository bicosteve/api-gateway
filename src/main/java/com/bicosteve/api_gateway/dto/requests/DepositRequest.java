package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.ValidPhoneNumber;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequest {
    @NotNull(message="amount is required")
    @DecimalMin(value="10.00", message = "minimum deposit is 10.00")
    @Schema(example = "500.00")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotBlank(message="email is required")
    @Email(message = "valid email is required")
    @Schema(example = "user@example.com")
    @JsonProperty("email")
    private String email;

    @NotBlank(message="first name is required")
    @Schema(example = "John")
    @JsonProperty("first-name")
    private String firstName;

    @NotBlank(message="last name is required")
    @Schema(example = "Doe")
    @JsonProperty("last-name")
    private String lastName;


    @Schema(example = "254701234567")
    @NotBlank(message = "Phone number is required")
    @Size(max = 12, message = "Phone number must be exactly 12 characters")
    @ValidPhoneNumber
    @JsonProperty("phone_number")
    private String phoneNumber;
}
