package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileResponse {
    private Long profileId;
    private String phoneNumber;

    private ProfileSettingsDto profileSettings;
}
