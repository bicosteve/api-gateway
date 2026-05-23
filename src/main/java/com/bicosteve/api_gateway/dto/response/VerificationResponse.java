package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Account verification response")
public class VerificationResponse {

    @Schema(example = "account verified")
    private String message;
}
