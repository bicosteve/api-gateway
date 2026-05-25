package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Server error response")
public class ServerErrorResponse {
    @Schema(example = "Internal server error")
    private String message;
}
