package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ProfileSetting response object")
public class ProfileSettingsResponse {
    @Schema(example = "1")
    private Integer status;

    @Schema(example = "0")
    private Integer isVerified;
}
