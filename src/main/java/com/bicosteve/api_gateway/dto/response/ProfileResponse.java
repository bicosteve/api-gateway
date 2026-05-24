package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Profile response object")
public class ProfileResponse {

    @Schema(example = "1")
    private Long profileId;

    @Schema(example = "254701962733")
    private String phoneNumber;

    private ProfileSettingsResponse profileSettings;
}
