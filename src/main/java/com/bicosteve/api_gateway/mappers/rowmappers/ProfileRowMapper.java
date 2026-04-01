package com.bicosteve.api_gateway.mappers.rowmappers;

import com.bicosteve.api_gateway.models.Profile;
import com.bicosteve.api_gateway.models.ProfileSettings;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class ProfileRowMapper implements RowMapper<Profile>{
    // Has mapRow method which assists in mapping the ResultSet inside the
    // ProfileRepository class
    // Hides the tedious column-to-field mapping

    @Override
    public Profile mapRow(ResultSet rs, int rowNum) throws SQLException{
        Profile profile = new Profile();

        // Map profile data
        profile.setProfileId(rs.getLong("profile_id"));
        profile.setPhoneNumber(rs.getString("phone_number"));
        profile.setPassword(rs.getString("password_hash"));
        profile.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        profile.setModifiedAt(rs.getObject("modified_at", LocalDateTime.class));

        // Check if the resultSet has settings data
        // If yes, maps the setting data in profile else ignore
        // This is to avoid runtime error of settings not being available in the result set.

        if(this.hasColumn(rs,"status")){
            // Map settings data
            ProfileSettings settings = new ProfileSettings();
            settings.setId(rs.getLong("settings_id"));
            settings.setStatus(rs.getInt("status"));
            settings.setIsVerified(rs.getInt("is_verified"));
            settings.setIsDeleted(rs.getInt("is_deleted"));
            settings.setProfileId(rs.getLong("profile_id"));
            settings.setCreateAt(rs.getObject("settings_created_at",LocalDateTime.class));
            settings.setModifiedAt(rs.getObject("settings_modified_at", LocalDateTime.class));

            // Combine inject settings into profile
            profile.setProfileSettings(settings);
        }

        return profile;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException{
        ResultSetMetaData metaData = rs.getMetaData();

        int columns = metaData.getColumnCount();

        for(int x = 1; x <= columns; x++){
            if(columnName.equalsIgnoreCase(metaData.getColumnName(x))){
                return true;
            }
        }

        return false;
    }
}
