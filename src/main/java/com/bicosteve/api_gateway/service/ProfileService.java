package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.LoginRequest;
import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.exceptions.InvalidOtpException;
import com.bicosteve.api_gateway.exceptions.PhoneNumberExistsException;
import com.bicosteve.api_gateway.exceptions.PhoneNumberNotFoundException;
import com.bicosteve.api_gateway.exceptions.ProfileNotFoundException;
import com.bicosteve.api_gateway.repository.JdbcProfileRepository;
import com.bicosteve.api_gateway.security.JwtConfig;
import com.bicosteve.api_gateway.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final JdbcProfileRepository profileRepository;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

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
        // 01. Check if the phone number exists in DB.
        // Throw error if exists to avoid duplicate registration.
        Optional<ProfileDto> profile = this.getProfileByPhoneNumber(request);
        if(profile.isPresent()){
            throw new PhoneNumberExistsException(request.getPhoneNumber());
        }

        // 02. Generate an OTP for verification
        String otp = this.otpService.generateAndStoreOtp(request.getPhoneNumber());

        // 03. Hash the password
        String hashedPassword = this.passwordEncoder.encode(request.getPassword());
        request.setPassword(hashedPassword);

        // 04. Insert the packaged user
        this.profileRepository.insertProfile(request);

        // 05. Return a map of message of success and otp
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
        ProfileDto profile = this.profileRepository.
                findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PhoneNumberNotFoundException("Profile with %s number does not exist".formatted(request.getPhoneNumber())));

        boolean isValid = this.otpService
                .verifyOtp(
                        request.getPhoneNumber(),
                        request.getVerificationCode()
                );

        if(!isValid){
            throw new InvalidOtpException("Provided otp %s is invalid".formatted(request.getVerificationCode()));
        }

        this.profileRepository.updateProfileStatus(1, 1, profile.getProfileId());

        return Map.of("msg","account verified");
    }

    /*
    * @generateLoginToken
    * @param LoginRequest
    * @param HttpServletResponse
    * */
    public Map<String, String> generateLoginToken(LoginRequest request, HttpServletResponse response){
        // 01. Check if the profile exists in the db
        ProfileDto profile = this.profileRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new PhoneNumberNotFoundException(request.getPhoneNumber()));


        // 02. Authenticate credentials (now we know user exists)
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );

        // 03. Generate tokens
        String accessToken = this.jwtService.generateAccessToken(profile);
        String refreshToken = this.jwtService.generateRefreshToken(profile);

        // 04. Store refresh token in HttpOnly cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/aut/refresh");
        cookie.setMaxAge(this.jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(false); // TODO -> Set to true in prod
        response.addCookie(cookie);

        return Map.of("access_token",accessToken, "refresh_token",refreshToken);
    }



}
