package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.exceptions.ProfileCreationException;
import com.bicosteve.api_gateway.exceptions.VerifyAccountException;
import com.bicosteve.api_gateway.mappers.ProfileMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
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
           return Optional.empty();
       }
    }

    public Optional<ProfileDto> findByPhoneNumber(String phoneNumber){
        String query = "SELECT profile_id, phone_number FROM profile WHERE phone_number = ?";
        try{
            ProfileDto profileDto = this.jdbcTemplate.queryForObject(
                    query,
                    (rs,rowNum)-> {
                        ProfileDto p = new ProfileDto();
                        p.setProfileId(rs.getLong("profile_id"));
                        p.setPhoneNumber(rs.getString("phone_number"));
                        return p;
                    }, phoneNumber
            );

            return Optional.ofNullable(profileDto);
        } catch(EmptyResultDataAccessException e) {
            return Optional.empty(); // no data found
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


            this.jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        query,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, request.getPhoneNumber());
                ps.setString(2, request.getPassword());
                ps.setTimestamp(3, Timestamp.valueOf(this.now));
                ps.setTimestamp(4, Timestamp.valueOf(this.now));
                return ps;
                    }, keyHolder);

            // 02. Get the generated id to insert into profile_settings
            Long profileId = (Long) keyHolder.getKeys().get("profile_id");

            // 03. Insert into the profile_settings
            String q = """
                        INSERT INTO profile_settings(status, is_verified, is_deleted, profile_id, created_at, modified_at) 
                        VALUES (?, ?, ?, ?, ?, ?)
                    """;

            this.jdbcTemplate.update(q, 0, 0, 0, profileId, Timestamp.valueOf(this.now),Timestamp.valueOf(this.now));

        }catch(DataAccessException ex){
            ex.printStackTrace();
            throw new ProfileCreationException(request.getPhoneNumber());
        }
    }

    public void updateProfileStatus(int status, int isVerified, Long profileId){
        String query = """
                    UPDATE profile_settings 
                    SET status = ?, is_verified = ? , modified_at = ?
                    WHERE profile_id = ?
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
            throw new VerifyAccountException(e.getMessage());
        }

    }


}
