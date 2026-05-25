package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Login response")
public class LoginResponse {

    @Schema(example = "eyJhbGci....")
    private String accessToken;

    @Schema(example = "eyJhbGci....")
    private String refreshToken;
}
