package com.bicosteve.api_gateway.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Not found response")
public class NotFoundResponse {

    @Schema(example = "404")
    private String statusCode;

    @Schema(example = "Not found")
    private String message;
}
