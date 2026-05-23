package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Bad request response")
public class BadRequestResponse {
    @Schema(example="Bad request")
    private String message;
}
