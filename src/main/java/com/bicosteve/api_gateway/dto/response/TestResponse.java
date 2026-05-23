package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Checking if the api is up and running")
public class TestResponse {
    @Schema(example = "App is running")
    private String message;
    private LocalDateTime timestamp;
}
