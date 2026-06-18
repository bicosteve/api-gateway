package com.bicosteve.api_gateway.repository;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.mappers.rowmappers.ProfileRowMapper;
import com.bicosteve.api_gateway.models.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Map;
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

        // Simulate the JDBC driver populating the KeyHolder with the generated id
        when(jdbcTemplate.update(any(PreparedStatementCreator.class), any(KeyHolder.class)))
                .thenAnswer(invocation -> {
                    KeyHolder keyHolder = invocation.getArgument(1);
                    Map<String, Object> keys = new HashMap<>();
                    keys.put("profile_id", 1L);
                    keyHolder.getKeyList().add(keys);
                    return 1;
                });

        repository.insertProfile(req);

        // The profile insert (via PreparedStatementCreator) and the
        // profile_settings insert (via varargs) are both executed
        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
        verify(jdbcTemplate).update(anyString(), any(), any(), any(), any(), any(), any());
    }
}
