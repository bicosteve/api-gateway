package com.bicosteve.api_gateway.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Not found (404) — returned when the requested resource does not exist")
public class NotFoundResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Human-readable error message", example = "Bet with bet id 1 not found")
    private String message;

    @Schema(description = "Timestamp of the error in dd-MM-yyyy HH:mm:ss format", example = "08-07-2026 01:18:29")
    private String timestamp;
}
