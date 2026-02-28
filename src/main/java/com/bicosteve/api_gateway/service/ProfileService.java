package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.response.Profile;
import com.bicosteve.api_gateway.repository.JdbcProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final JdbcProfileRepository profileRepository;

    /*
    * Find profile by its ID
    * @param id the profile_id to search for
    * @return Optional containing the profile found or empty if not
    * */

    public Optional<Profile> getProfileById(Long id){
        return this.profileRepository.findById(id);
    }

    /*
     * Find profile by phoneNumber
     * @param RegisterRequest which has the phone_number to search
     * @return Optional containing the profile found or empty if not
     * */
    public Optional<Profile> getProfileByEmail(RegisterRequest request){
        return this.profileRepository.findByPhoneNumber(request.getPhoneNumber());
    }

}
