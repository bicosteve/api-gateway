package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.exceptions.InvalidOtpException;
import com.bicosteve.api_gateway.exceptions.PhoneNumberExistsException;
import com.bicosteve.api_gateway.exceptions.PhoneNumberNotFoundException;
import com.bicosteve.api_gateway.exceptions.ProfileNotFoundException;
import com.bicosteve.api_gateway.repository.JdbcProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final JdbcProfileRepository profileRepository;
    private final OtpService otpService;

    /*
    * Find profile by its ID
    * @param id the profile_id to search for
    * @return Optional containing the profile found or empty if not
    * */
    public ProfileDto getProfileById(Long id){
        return this.profileRepository.
                findById(id).
                orElseThrow(() -> new ProfileNotFoundException(id));
    }

    /*
     * Find profile by phoneNumber
     * @param RegisterRequest which has the phone_number to search
     * @return Optional containing the profile found or empty if not
     * */
    private Optional<ProfileDto> getProfileByPhoneNumber(RegisterRequest request){
        return this.profileRepository.findByPhoneNumber(request.getPhoneNumber());
    }


    /*
     * CreateProfile
     * @param RegisterRequest which has the phone_number and password to insert
     * */
    public Map<String,String> createProfile(RegisterRequest request){
        this.getProfileByPhoneNumber(request).ifPresent(profile -> {
            throw new PhoneNumberExistsException(request.getPhoneNumber());
        });
        this.profileRepository.insertProfile(request);
        String otp = this.otpService.generateAndStoreOtp(request.getPhoneNumber());
        return Map.of(
                "message","Registration success",
                "verification_code",otp
        );
    }

    /*
     * @verifyProfile
     * @param RegisterRequest which has the phone_number and password to insert
     * */
    public Map<String,String> verifyProfile(VerifyRequest request){
        var profile = this.profileRepository.findByPhoneNumber(request.getPhoneNumber());
        if(profile.isEmpty()){
            throw new PhoneNumberNotFoundException("Profile with %s number does not exist".formatted(request.getPhoneNumber()));
        }

        boolean isValid = this.otpService.verifyOtp(request.getPhoneNumber(), request.getVerificationCode());
        if(!isValid){
            throw new InvalidOtpException("Provided otp %s is invalid".formatted(request.getVerificationCode()));
        }

        this.profileRepository.updateProfileStatus(1, 1, profile.get().getProfileId());

        return Map.of("msg","account verified");
    }



}
