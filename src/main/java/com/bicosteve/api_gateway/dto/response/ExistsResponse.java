package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Conflict (409) — returned when a resource already exists, e.g. a duplicate phone number")
public class ExistsResponse {

    @Schema(description = "HTTP status code", example = "409")
    private int status;

    @Schema(description = "Human-readable error message", example = "The phone number 254701234567 already exists")
    private String message;

    @Schema(description = "Timestamp of the error in dd-MM-yyyy HH:mm:ss format", example = "08-07-2026 01:18:29")
    private String timestamp;
}
