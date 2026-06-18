package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.requests.LoginRequest;
import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.dto.response.LoginResponse;
import com.bicosteve.api_gateway.dto.response.ProfileResponse;
import com.bicosteve.api_gateway.dto.response.RegisterResponse;
import com.bicosteve.api_gateway.dto.response.VerificationResponse;
import com.bicosteve.api_gateway.exceptions.InvalidOtpException;
import com.bicosteve.api_gateway.exceptions.PhoneNumberExistsException;
import com.bicosteve.api_gateway.exceptions.PhoneNumberNotFoundException;
import com.bicosteve.api_gateway.exceptions.ProfileNotFoundException;
import com.bicosteve.api_gateway.mappers.dtomappers.ProfileDtoMapper;
import com.bicosteve.api_gateway.models.Profile;
import com.bicosteve.api_gateway.models.ProfileSettings;
import com.bicosteve.api_gateway.repository.ProfileRepository;
import com.bicosteve.api_gateway.security.CustomUserDetails;
import com.bicosteve.api_gateway.security.JwtConfig;
import com.bicosteve.api_gateway.security.JwtService;
import com.bicosteve.api_gateway.utils.MailgunService;
import com.bicosteve.api_gateway.utils.OtpService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private ProfileRepository profileRepository;
    private OtpService otpService;
    private AuthenticationManager authManager;
    private JwtService jwtService;
    private JwtConfig jwtConfig;
    private PasswordEncoder passwordEncoder;
    private MailgunService mailgunService;
    private ProfileDtoMapper profileDtoMapper;
    private ProfileService service;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        otpService = mock(OtpService.class);
        authManager = mock(AuthenticationManager.class);
        jwtConfig = new JwtConfig();
        jwtConfig.setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        jwtConfig.setAccessTokenExpiration(60);
        jwtConfig.setRefreshTokenExpiration(3600);
        jwtService = new JwtService(jwtConfig);
        passwordEncoder = mock(PasswordEncoder.class);
        mailgunService = mock(MailgunService.class);
        profileDtoMapper = new ProfileDtoMapper();

        service = new ProfileService(profileRepository, otpService, authManager,
                jwtService, jwtConfig, passwordEncoder, mailgunService, profileDtoMapper);
    }

    private Profile sampleProfile() {
        return Profile.builder()
                .profileId(7L)
                .phoneNumber("254701234567")
                .password("encoded")
                .createdAt(LocalDateTime.now())
                .profileSettings(ProfileSettings.builder().status(1).isVerified(1).isDeleted(0).build())
                .build();
    }

    private CustomUserDetails userDetails(Profile p) {
        return new CustomUserDetails(p.getProfileId(), p.getPhoneNumber(),
                1, 1, 0, p.getPassword(), List.of());
    }

    @Test
    void getProfileByIdReturnsProfileResponse() {
        Profile profile = sampleProfile();
        when(profileRepository.findById(7L)).thenReturn(Optional.of(profile));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails(profile), null, List.of());
        ProfileResponse response = service.getProfileById(auth);

        assertEquals(7L, response.getProfileId());
        assertEquals("254701234567", response.getPhoneNumber());
    }

    @Test
    void getProfileByIdThrowsWhenNotFound() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        Profile p = Profile.builder().profileId(99L).phoneNumber("x").password("encoded").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails(p), null, List.of());

        assertThrows(ProfileNotFoundException.class, () -> service.getProfileById(auth));
    }

    @Test
    void createProfileSucceedsAndSendsOtp() {
        RegisterRequest req = new RegisterRequest("254701234567", "a@b.com", "pass1234", "pass1234");
        when(profileRepository.existsByPhoneNumber("254701234567")).thenReturn(false);
        when(otpService.generateAndStoreOtp("254701234567")).thenReturn("123456");
        when(passwordEncoder.encode("pass1234")).thenReturn("encoded");

        RegisterResponse response = service.createProfile(req);

        assertEquals("Registration success", response.getMessage());
        assertEquals("123456", response.getVerificationCode());
        verify(profileRepository).insertProfile(req);
        verify(mailgunService).sendEmail(any());
    }

    @Test
    void createProfileThrowsWhenPhoneExists() {
        RegisterRequest req = new RegisterRequest("254701234567", "a@b.com", "pass1234", "pass1234");
        when(profileRepository.existsByPhoneNumber("254701234567")).thenReturn(true);

        assertThrows(PhoneNumberExistsException.class, () -> service.createProfile(req));
        verify(otpService, never()).generateAndStoreOtp(anyString());
    }

    @Test
    void verifyProfileSucceeds() {
        VerifyRequest req = new VerifyRequest("254701234567", "123456");
        when(profileRepository.findByPhoneNumber("254701234567")).thenReturn(Optional.of(sampleProfile()));
        when(otpService.verifyOtp("254701234567", "123456")).thenReturn(true);

        VerificationResponse response = service.verifyProfile(req);

        assertEquals("Profile verified", response.getMessage());
        verify(profileRepository).updateProfileStatus(1, 1, 7L);
    }

    @Test
    void verifyProfileThrowsWhenPhoneNotFound() {
        VerifyRequest req = new VerifyRequest("254701234567", "123456");
        when(profileRepository.findByPhoneNumber("254701234567")).thenReturn(Optional.empty());

        assertThrows(PhoneNumberNotFoundException.class, () -> service.verifyProfile(req));
    }

    @Test
    void verifyProfileThrowsWhenOtpInvalid() {
        VerifyRequest req = new VerifyRequest("254701234567", "123456");
        when(profileRepository.findByPhoneNumber("254701234567")).thenReturn(Optional.of(sampleProfile()));
        when(otpService.verifyOtp("254701234567", "123456")).thenReturn(false);

        assertThrows(InvalidOtpException.class, () -> service.verifyProfile(req));
    }

    @Test
    void generateLoginTokenSucceeds() {
        LoginRequest req = new LoginRequest("254701234567", "pass1234");
        when(profileRepository.findByPhoneNumber("254701234567")).thenReturn(Optional.of(sampleProfile()));
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        LoginResponse login = service.generateLoginToken(req, mockResponse);

        assertNotNull(login.getAccessToken());
        assertNotNull(login.getRefreshToken());
        assertEquals(1, mockResponse.getCookies().length);
        assertEquals("refreshToken", mockResponse.getCookies()[0].getName());
        assertTrue(mockResponse.getCookies()[0].isHttpOnly());
    }

    @Test
    void generateLoginTokenThrowsWhenPhoneNotFound() {
        LoginRequest req = new LoginRequest("254701234567", "pass1234");
        when(profileRepository.findByPhoneNumber("254701234567")).thenReturn(Optional.empty());
        HttpServletResponse response = new MockHttpServletResponse();

        assertThrows(PhoneNumberNotFoundException.class,
                () -> service.generateLoginToken(req, response));
    }
}
