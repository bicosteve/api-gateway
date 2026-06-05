package com.bicosteve.api_gateway.mappers.dtomappers;

import com.bicosteve.api_gateway.dto.response.ProfileResponse;
import com.bicosteve.api_gateway.dto.response.ProfileSettingsResponse;
import com.bicosteve.api_gateway.models.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileDtoMapper{
    public ProfileResponse toDto(Profile profile){
        if(profile == null) return null;

        ProfileResponse dto = new ProfileResponse();
        dto.setProfileId(profile.getProfileId());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setCreatedAt(profile.getCreatedAt());

        // Map settings to do if setting exists
        if(profile.getProfileSettings() != null){
            ProfileSettingsResponse settingsDto = new ProfileSettingsResponse();
            settingsDto.setStatus(profile.getProfileSettings().getStatus());
            settingsDto.setIsVerified(profile.getProfileSettings().getIsVerified());
            // settingsDto.setIsDeleted(profile.getProfileSettings().getIsDeleted());
            // Ignore setIsDeleted since we do not want to send it to client
            dto.setProfileSettings(settingsDto);
        }
        return dto;
    }
}
