package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@Schema(description = "Bad request (400) — returned when the request fails validation or violates a business rule")
public class BadRequestResponse {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Human-readable error message", example = "Validation failed")
    private String message;

    @Schema(description = "Timestamp of the error in dd-MM-yyyy HH:mm:ss format", example = "08-07-2026 01:18:29")
    private String timestamp;

    @Schema(
            description = "Field-level validation errors (present only for @Valid failures); null otherwise",
            example = "Error validating your request."
    )
    private Map<String, String> validationErrors;
}
