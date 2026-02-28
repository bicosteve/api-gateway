package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.response.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcProfileRepository {
    private final JdbcTemplate jdbcTemplate;


    public Optional<Profile> findById(Long id){
        String query = "SELECT * FROM profile WHERE profile_id = ?";
       try{
           Profile profile =  this.jdbcTemplate.queryForObject(
                   query,
                   new Object[]{id},
                   (rs, rowNum) -> new Profile(
                           rs.getLong("profile_id"),
                           rs.getString("phone_number"),
                           rs.getString("password_hash"),
                           rs.getTimestamp("created_at"),
                           rs.getTimestamp("modified_at")
                   )
           );
           return Optional.of(profile);
       } catch(EmptyResultDataAccessException e) {
           return Optional.empty();
       }
    }

    public Optional<Profile> findByPhoneNumber(String phoneNumber){
        String query = "SELECT profile_id, phone_number FROM profile WHERE phone_number = ?";
        try{
            Profile profile = this.jdbcTemplate.queryForObject(
                    query,
                    new Object[]{phoneNumber},
                    (rs,rowNum)-> {
                        Profile p = new Profile();
                        p.setProfileId(rs.getLong("profile_id"));
                        p.setPhoneNumber(rs.getString("phone_number"));
                        return p;
                    }
            );

            return Optional.ofNullable(profile);
        } catch(Exception e) {
            throw new RuntimeException("Error finding profile by phone number",e);
        }
    }


}
