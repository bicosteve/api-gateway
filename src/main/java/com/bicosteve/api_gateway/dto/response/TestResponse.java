package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Test response object")
public class TestResponse {
    @Schema(example = "App is running")
    private String message;

    @Schema(example = "2026-05-07 04:46:31")
    private LocalDateTime timestamp;
}
