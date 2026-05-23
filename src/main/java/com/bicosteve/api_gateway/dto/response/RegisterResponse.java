package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "User register response")
public class RegisterResponse {
    @Schema(example = "User registered successfully")
    private String message;

    @Schema(example = "123456")
    private String verificationCode;
}
