package com.bicosteve.api_gateway.mappers.dtomappers;

import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.dto.response.ProfileSettingsDto;
import com.bicosteve.api_gateway.models.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileDtoMapper{
    public ProfileDto toDto(Profile profile){
        if(profile == null) return null;

        ProfileDto dto = new ProfileDto();
        dto.setProfileId(profile.getProfileId());
        dto.setPhoneNumber(profile.getPhoneNumber());
        // Ignore password_hash since we do not want to send it to user

        // Map settings to do if setting exists
        if(profile.getProfileSettings() != null){
            ProfileSettingsDto settingsDto = new ProfileSettingsDto();
            settingsDto.setStatus(profile.getProfileSettings().getStatus());
            settingsDto.setIsVerified(profile.getProfileSettings().getIsVerified());

            // Ignore setIsDeleted since we do not want to send it to client

            dto.setProfileSettings(settingsDto);
        }

        return dto;
    }
}
