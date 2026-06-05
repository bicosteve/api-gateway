package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Schema(example = "2026-05-08 05:52:30")
    private LocalDateTime createdAt;
}
