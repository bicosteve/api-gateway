package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "Response for when user already exists")
@Builder
public class ExistsResponse {
    @Schema(example = "User already exists")
    private String message;
}
