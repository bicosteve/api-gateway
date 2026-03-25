package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.dto.response.ProfileSettingsDto;
import com.bicosteve.api_gateway.exceptions.ProfileCreationException;
import com.bicosteve.api_gateway.exceptions.VerifyAccountException;
import com.bicosteve.api_gateway.mappers.ProfileMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class JdbcProfileRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProfileMapper profileMapper;
    private final LocalDateTime now = LocalDateTime.now();


    public Optional<ProfileDto> findById(Long id){
        String query = "SELECT * FROM profile WHERE profile_id = ?";
       try{
           ProfileDto profileDto =  this.jdbcTemplate
                   .queryForObject(query, this.profileMapper::toDto, id);
           return Optional.ofNullable(profileDto);
       } catch(EmptyResultDataAccessException e) {
           log.info("ProfileRepository::no profile found for {}",id);
           return Optional.empty();
       }
    }

    public Optional<ProfileDto> findByPhoneNumber(String phoneNumber){
        String query = """
                    SELECT
                        p.profile_id,
                        p.phone_number,
                        p.password_hash,
                        ps.status,
                        ps.is_verified,
                        ps.is_deleted
                    FROM profile p
                    LEFT JOIN profile_settings ps
                        ON p.profile_id = ps.profile_id
                    WHERE p.phone_number = ?
                    LIMIT 1
                """;
        try{
            ProfileDto profileDto = this.jdbcTemplate.queryForObject(
                    query,
                    (rs,rowNum)-> {
                        ProfileDto p = new ProfileDto();
                        p.setProfileId(rs.getLong("profile_id"));
                        p.setPhoneNumber(rs.getString("phone_number"));
                        p.setPassword(rs.getString("password_hash"));

                        // Check if the settings exist before creating the object
                        if(rs.getObject("settings_id") != null){
                            ProfileSettingsDto ps = new ProfileSettingsDto();
                            ps.setStatus(rs.getInt("status"));
                            ps.setIsVerified(rs.getInt("is_verified"));
                            ps.setIsDeleted(rs.getInt("is_deleted"));

                            p.setProfileSettings(ps);
                        } else {
                            p.setProfileSettings(null);
                        }

                        return p;
                    }, phoneNumber
            );
            return Optional.ofNullable(profileDto);
        } catch(EmptyResultDataAccessException e) {
            log.info("ProfileRepository::No profile found for {}",phoneNumber);
            return Optional.empty();
        }
    }

    @Transactional
    public void insertProfile(RegisterRequest request){
        try{
            // 01. Insert profile and get generated profile_id
            String query = """
                        INSERT INTO profile(phone_number, password_hash, created_at, modified_at)
                        VALUES (?,?,?,?)
                    """;
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = this.jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        query,
                        new String[]{"profile_id"}
                );

                ps.setString(1, request.getPhoneNumber());
                ps.setString(2, request.getPassword());
                ps.setTimestamp(3, Timestamp.valueOf(this.now));
                ps.setTimestamp(4, Timestamp.valueOf(this.now));

                return ps;

                }, keyHolder);

            // 02. Get the generated id to insert into profile_settings
            Long profileId = null;
            if(rowsAffected == 1 && keyHolder.getKey() != null){
                profileId = keyHolder.getKey().longValue();
            }

            // 02b. Guard profileId before using it
            // Throws exception if the profileId is null
            if(profileId == null){
                throw new ProfileCreationException(request.getPhoneNumber());
            }

            // 03. Insert into the profile_settings
            String q = """
                        INSERT INTO
                         profile_settings(status, is_verified, is_deleted, profile_id, created_at, modified_at)
                        VALUES (?, ?, ?, ?, ?, ?)
                    """;

            this.jdbcTemplate.update(q, 0, 0, 0, profileId, Timestamp.valueOf(this.now),Timestamp.valueOf(this.now));

        }catch(DataAccessException ex){
            log.warn("ProfileRepository::Error {} getting profile",ex.getMessage());
            throw new ProfileCreationException(request.getPhoneNumber());
        }
    }

    public void updateProfileStatus(int status, int isVerified, Long profileId){
        String query = """
                    UPDATE
                        profile_settings
                    SET
                        status = ?,
                        is_verified = ?,
                        modified_at = ?
                    WHERE
                        profile_id = ?
                """;

        try{
            this.jdbcTemplate.update(
                    query,
                    status,
                    isVerified,
                    this.now,
                    profileId
            );
        } catch(DataAccessException e) {
            log.warn("ProfileRepository::Error {} updating profile",e.getMessage());
            throw new VerifyAccountException(e.getMessage());
        }

    }


}
