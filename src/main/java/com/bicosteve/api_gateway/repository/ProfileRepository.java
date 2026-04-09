package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.exceptions.ProfileCreationException;
import com.bicosteve.api_gateway.exceptions.VerifyAccountException;
import com.bicosteve.api_gateway.mappers.rowmappers.ProfileRowMapper;
import com.bicosteve.api_gateway.models.Profile;
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
public class ProfileRepository{
    private final JdbcTemplate jdbcTemplate;
    private final ProfileRowMapper profileRowMapper;
    private final LocalDateTime timeNow = LocalDateTime.now();


    public Optional<Profile> findById(Long id){
        String query = "SELECT * FROM profile WHERE profile_id = ?";
       try{
           Profile profile = this.jdbcTemplate
                   .queryForObject(query,
                   this.profileRowMapper,
                   id
           );

           return Optional.ofNullable(profile);
       } catch(EmptyResultDataAccessException e) {
           log.info("ProfileRepository::no profile found for {}",id);
           return Optional.empty();
       }
    }

    public Optional<Profile> findByPhoneNumber(String phoneNumber){
        String query = """
                    SELECT
                        p.profile_id,
                        p.phone_number,
                        p.password_hash,
                        p.created_at,
                        p.modified_at,
                        ps.id AS settings_id,
                        ps.status,
                        ps.is_verified,
                        ps.is_deleted,
                        ps.profile_id AS settings_profile_id,
                        ps.created_at AS settings_created_at,
                        ps.modified_at AS settings_modified_at
                    FROM profile p
                    LEFT JOIN profile_settings ps
                        ON p.profile_id = ps.profile_id
                    WHERE p.phone_number = ?
                    LIMIT 1
                """;
        try{
            Profile profile = this.jdbcTemplate.queryForObject(
                    query,
                    this.profileRowMapper,
                    phoneNumber
            );

            log.info("ProfileRepository::profile data {}",profile);

            return Optional.ofNullable(profile);

        } catch(EmptyResultDataAccessException e) {
            log.info("ProfileRepository::No profile found for {}",phoneNumber);
            return Optional.empty();
        }
    }

    public boolean existsByPhoneNumber(String phoneNumber){
        String sql = "SELECT COUNT(*) FROM profile WHERE phone_number = ?";
        Integer count = this.jdbcTemplate.queryForObject(sql,Integer.class,phoneNumber);
        return count != null && count > 0;
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
                ps.setTimestamp(3, Timestamp.valueOf(this.timeNow));
                ps.setTimestamp(4, Timestamp.valueOf(this.timeNow));

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

            this.jdbcTemplate.update(q, 0, 0, 0, profileId, Timestamp.valueOf(this.timeNow),Timestamp.valueOf(this.timeNow));

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
                    this.timeNow,
                    profileId
            );
        } catch(DataAccessException e) {
            log.warn("ProfileRepository::Error {} updating profile",e.getMessage());
            throw new VerifyAccountException(e.getMessage());
        }

    }


}
