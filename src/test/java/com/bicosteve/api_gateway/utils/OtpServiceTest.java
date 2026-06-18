package com.bicosteve.api_gateway.utils;

import com.bicosteve.api_gateway.exceptions.InvalidOtpException;
import com.bicosteve.api_gateway.exceptions.OtpExpiredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    private RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOps;
    private OtpService otpService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        otpService = new OtpService(redisTemplate);
    }

    @Test
    void generateAndStoreOtpStoresInRedisAndReturnsOtp() {
        String otp = otpService.generateAndStoreOtp("254701234567");

        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(valueOps).set(
                eq("otp:254701234567"),
                eq(otp),
                eq(5L),
                eq(TimeUnit.HOURS)
        );
    }

    @Test
    void verifyOtpReturnsTrueAndDeletesOnMatch() {
        when(valueOps.get("otp:254701234567")).thenReturn("123456");

        boolean result = otpService.verifyOtp("254701234567", "123456");

        assertTrue(result);
        verify(redisTemplate).delete("otp:254701234567");
    }

    @Test
    void verifyOtpThrowsOtpExpiredWhenStoredIsNull() {
        when(valueOps.get("otp:254701234567")).thenReturn(null);

        assertThrows(OtpExpiredException.class,
                () -> otpService.verifyOtp("254701234567", "123456"));
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void verifyOtpThrowsInvalidOtpWhenMismatch() {
        when(valueOps.get("otp:254701234567")).thenReturn("111111");

        assertThrows(InvalidOtpException.class,
                () -> otpService.verifyOtp("254701234567", "222222"));
        verify(redisTemplate, never()).delete(anyString());
    }
}
