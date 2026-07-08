package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Unauthorized (401) — returned when the request lacks a valid access token or credentials")
public class UnauthorizedResponse {

    @Schema(description = "HTTP status code", example = "401")
    private int status;

    @Schema(description = "Human-readable error message", example = "Expired access token or refresh token is invalid")
    private String message;

    @Schema(description = "Timestamp of the error in dd-MM-yyyy HH:mm:ss format", example = "08-07-2026 01:18:29")
    private String timestamp;
}
