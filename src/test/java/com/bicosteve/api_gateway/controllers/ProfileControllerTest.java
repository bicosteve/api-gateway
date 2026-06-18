package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.response.ProfileResponse;
import com.bicosteve.api_gateway.security.CustomUserDetails;
import com.bicosteve.api_gateway.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    private ProfileService profileService;
    private ProfileController controller;

    @BeforeEach
    void setUp() {
        profileService = mock(ProfileService.class);
        controller = new ProfileController(profileService);
    }

    @Test
    void getMeReturnsOk() {
        CustomUserDetails cud = new CustomUserDetails(7L, "254701234567",
                1, 1, 0, "x", List.of());
        Authentication auth = new UsernamePasswordAuthenticationToken(cud, null, List.of());

        ProfileResponse response = new ProfileResponse();
        response.setProfileId(7L);
        when(profileService.getProfileById(auth)).thenReturn(response);

        ResponseEntity<ProfileResponse> r = controller.getMe(auth);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(7L, r.getBody().getProfileId());
    }
}
