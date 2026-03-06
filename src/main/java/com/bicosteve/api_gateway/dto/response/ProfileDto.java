package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileDto {
    private Long profileId;
    private String phoneNumber;
    private String password;
    private Date createdAt;
    private Date modifiedAt;

    private ProfileSettingsDto profileSettings;
}
