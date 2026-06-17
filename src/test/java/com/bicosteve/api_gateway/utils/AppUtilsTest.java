package com.bicosteve.api_gateway.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class AppUtilsTest {

    private static final Pattern OTP_PATTERN = Pattern.compile("\\d{6}");

    @Test
    void generateOtpReturnsSixDigits() {
        String otp = AppUtils.generateOTP();
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(OTP_PATTERN.matcher(otp).matches());
    }

    @Test
    void generateOtpReturnsValueInExpectedRange() {
        for (int i = 0; i < 50; i++) {
            int otp = Integer.parseInt(AppUtils.generateOTP());
            assertTrue(otp >= 100_000, "OTP below 100k: " + otp);
            assertTrue(otp <= 999_999, "OTP above 999999: " + otp);
        }
    }

    @Test
    void generateOtpProducesDiverseValues() {
        Set<String> otps = new HashSet<>();
        for (int i = 0; i < 200; i++) {
            otps.add(AppUtils.generateOTP());
        }
        // With 200 random 6-digit values we expect many distinct values
        assertTrue(otps.size() > 100, "OTPs should be diverse");
    }
}
