package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.mappers.rowmappers.ProfileRowMapper;
import com.bicosteve.api_gateway.models.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProfileRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private ProfileRowMapper profileRowMapper;
    private ProfileRepository repository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        profileRowMapper = mock(ProfileRowMapper.class);
        repository = new ProfileRepository(jdbcTemplate, profileRowMapper);
    }

    @Test
    void existsByPhoneNumberReturnsTrueWhenCountPositive() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("254701234567")))
                .thenReturn(1);
        assertTrue(repository.existsByPhoneNumber("254701234567"));
    }

    @Test
    void existsByPhoneNumberReturnsFalseWhenCountZero() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("254701234567")))
                .thenReturn(0);
        assertFalse(repository.existsByPhoneNumber("254701234567"));
    }

    @Test
    void existsByPhoneNumberHandlesNullCount() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("254701234567")))
                .thenReturn(null);
        assertFalse(repository.existsByPhoneNumber("254701234567"));
    }

    @Test
    void findByPhoneNumberReturnsProfile() {
        Profile p = Profile.builder().profileId(1L).phoneNumber("254701234567").build();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("254701234567")))
                .thenReturn(p);
        Optional<Profile> result = repository.findByPhoneNumber("254701234567");
        assertTrue(result.isPresent());
        assertSame(p, result.get());
    }

    @Test
    void findByPhoneNumberReturnsEmptyOnException() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("x")))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));
        assertFalse(repository.findByPhoneNumber("x").isPresent());
    }

    @Test
    void findByIdReturnsProfile() {
        Profile p = Profile.builder().profileId(5L).phoneNumber("x").build();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(5L)))
                .thenReturn(p);
        Optional<Profile> result = repository.findById(5L);
        assertTrue(result.isPresent());
        assertSame(p, result.get());
    }

    @Test
    void findByIdReturnsEmptyOnException() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(99L)))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));
        assertFalse(repository.findById(99L).isPresent());
    }

    @Test
    void insertProfileExecutesUpdate() {
        RegisterRequest req = new RegisterRequest("254701234567", "a@b.com", "hashed", "hashed");
        repository.insertProfile(req);
        // Two update statements are issued: one for profile and one for profile_settings
        verify(jdbcTemplate, atLeastOnce()).update(anyString(), (Object[]) any());
    }
}
