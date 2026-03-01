package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.mappers.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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


}
