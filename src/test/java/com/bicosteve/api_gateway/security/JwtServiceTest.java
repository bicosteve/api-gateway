package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.models.Profile;
import com.bicosteve.api_gateway.models.ProfileSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtConfig jwtConfig;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        // 64 byte secret to satisfy HS512 key length requirement (>=64 bytes for HS512)
        jwtConfig.setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        jwtConfig.setAccessTokenExpiration(60); // 60 seconds
        jwtConfig.setRefreshTokenExpiration(3600); // 1 hour
        jwtService = new JwtService(jwtConfig);
    }

    private Profile sampleProfile() {
        ProfileSettings settings = ProfileSettings.builder()
                .status(1)
                .isVerified(1)
                .isDeleted(0)
                .build();
        return Profile.builder()
                .profileId(7L)
                .phoneNumber("254701234567")
                .profileSettings(settings)
                .build();
    }

    @Test
    void generateAccessTokenReturnsValidJwt() {
        String token = jwtService.generateAccessToken(sampleProfile());
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void generateRefreshTokenReturnsValidJwt() {
        String token = jwtService.generateRefreshToken(sampleProfile());
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void getPhoneNumberFromTokenExtractsClaim() {
        Profile profile = sampleProfile();
        String token = jwtService.generateAccessToken(profile);
        String phone = jwtService.getPhoneNumberFromToken(token);
        assertEquals("254701234567", phone);
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        String token = jwtService.generateAccessToken(sampleProfile());
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateTokenReturnsFalseForInvalidToken() {
        assertFalse(jwtService.validateToken("this.is.notvalid"));
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() {
        // Configure an already expired token
        jwtConfig.setAccessTokenExpiration(-10);
        jwtConfig.setRefreshTokenExpiration(-10);
        String token = jwtService.generateAccessToken(sampleProfile());
        assertFalse(jwtService.validateToken(token));
    }

    @Test
    void validateTokenReturnsFalseForEmptyToken() {
        assertFalse(jwtService.validateToken(""));
    }

    @Test
    void validateTokenReturnsFalseForNullToken() {
        assertFalse(jwtService.validateToken(null));
    }
}
