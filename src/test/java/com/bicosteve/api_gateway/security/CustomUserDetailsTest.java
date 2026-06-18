package com.bicosteve.api_gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void constructorAndGettersExposeAllFields() {
        List<GrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        CustomUserDetails cud = new CustomUserDetails(
                42L, "254701234567", 1, 1, 0, "secret", auths
        );

        assertEquals(42L, cud.getProfileId());
        assertEquals("254701234567", cud.getPhoneNumber());
        assertEquals(1, cud.getStatus());
        assertEquals(1, cud.getIsVerified());
        assertEquals(0, cud.getIsDeleted());
        assertEquals("254701234567", cud.getUsername());
        assertEquals("secret", cud.getPassword());
        assertEquals(1, cud.getAuthorities().size());
    }

    @Test
    void isAccountNonExpiredAndNonLockedDefaultToTrue() {
        CustomUserDetails cud = new CustomUserDetails(
                1L, "u", 1, 1, 0, "p", List.of()
        );
        assertTrue(cud.isAccountNonExpired());
        assertTrue(cud.isAccountNonLocked());
        assertTrue(cud.isCredentialsNonExpired());
        assertTrue(cud.isEnabled());
    }
}
