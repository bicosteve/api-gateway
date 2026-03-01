package com.bicosteve.api_gateway.mappers;

import com.bicosteve.api_gateway.dto.response.ProfileDto;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProfileMapper {
    public ProfileDto toDto(ResultSet rs, int rowNum) throws SQLException{
        ProfileDto dto = new ProfileDto();
        dto.setProfileId(rs.getLong("profile_id"));
        dto.setPhoneNumber(rs.getString("phone_number"));
        dto.setCreatedAt(rs.getTimestamp("created_at"));
        dto.setModifiedAt(rs.getTimestamp("modified_at"));
        return dto;
    }
}
