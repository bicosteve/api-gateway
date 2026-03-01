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
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcProfileRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ProfileMapper profileMapper;


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
            String query = "INSERT INTO profile(phone_number, password_hash) VALUES (?,?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            this.jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        query,
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, request.getPhoneNumber());
                ps.setString(2, request.getPassword());
                return ps;
                    }, keyHolder);

            // 02. Get the generated id to insert into profile_settings
            Long profileId = keyHolder.getKey().longValue();

            // 03. Insert into the profile_settings
            String q = """
                        INSERT INTO profile_settings(status, is_verified, is_deleted, profile_id) 
                        VALUES (?, ?, ?, ?)
                    """;

            this.jdbcTemplate.update(q, 0, 0, 0, profileId);

        }catch(DataAccessException ex){
            throw new ProfileCreationException(request.getPhoneNumber());
        }
    }

    public void updateProfileStatus(Long status, Long isVerified, Long profileId){
        String query = """
                    UPDATE profile_settings SET status = ?, is_verified = ? 
                    WHERE profile_id = ?
                """;

        try{
            this.jdbcTemplate.update(
                    query,
                    status,
                    isVerified,
                    profileId
            );
        } catch(DataAccessException e) {
            throw new VerifyAccountException(e.getMessage());
        }

    }


}
