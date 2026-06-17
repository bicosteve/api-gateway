package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.models.Profile;
import com.bicosteve.api_gateway.models.ProfileSettings;
import com.bicosteve.api_gateway.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private ProfileRepository profileRepository;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        service = new UserDetailsServiceImpl(profileRepository);
    }

    private Profile sampleProfile() {
        ProfileSettings settings = ProfileSettings.builder()
                .status(1).isVerified(1).isDeleted(0).build();
        return Profile.builder()
                .profileId(5L)
                .phoneNumber("254701234567")
                .password("hash")
                .profileSettings(settings)
                .build();
    }

    @Test
    void loadUserByUsernameReturnsCustomUserDetails() {
        when(profileRepository.findByPhoneNumber("254701234567"))
                .thenReturn(Optional.of(sampleProfile()));

        UserDetails details = service.loadUserByUsername("254701234567");

        assertTrue(details instanceof CustomUserDetails);
        CustomUserDetails cud = (CustomUserDetails) details;
        assertEquals(5L, cud.getProfileId());
        assertEquals("254701234567", cud.getPhoneNumber());
        assertEquals(1, cud.getStatus());
        assertEquals(1, cud.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter("ROLE_USER"::equals)
                .count());
    }

    @Test
    void loadUserByUsernameThrowsWhenNotFound() {
        when(profileRepository.findByPhoneNumber("missing"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing"));
    }
}
