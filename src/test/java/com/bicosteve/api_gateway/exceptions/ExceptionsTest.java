package com.bicosteve.api_gateway.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void badSqlGrammaException() {
        BadSqlGrammaException ex = new BadSqlGrammaException("bad");
        assertTrue(ex.getMessage().contains("bad"));
    }

    @Test
    void betNotFoundException() {
        BetNotFoundException ex = new BetNotFoundException("42");
        assertTrue(ex.getMessage().contains("42"));
    }

    @Test
    void errorResponseBuilder() {
        ErrorResponse e = ErrorResponse.builder()
                .status(400).message("bad").timestamp("now").build();
        assertEquals(400, e.getStatus());
        assertEquals("bad", e.getMessage());
        assertEquals("now", e.getTimestamp());
    }

    @Test
    void eventNotFoundException() {
        EventNotFoundException ex = new EventNotFoundException("e1");
        assertTrue(ex.getMessage().contains("e1"));
    }

    @Test
    void expiredEventException() {
        ExpiredEventException ex = new ExpiredEventException("e1");
        assertTrue(ex.getMessage().contains("e1"));
    }

    @Test
    void illegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("bad");
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void invalidOtpException() {
        InvalidOtpException ex = new InvalidOtpException("bad");
        assertEquals("bad", ex.getMessage());
    }

    @Test
    void otpExpiredException() {
        OtpExpiredException ex = new OtpExpiredException("expired");
        assertEquals("expired", ex.getMessage());
    }

    @Test
    void invalidTokenException() {
        InvalidTokenException ex = new InvalidTokenException("bad token");
        assertEquals("bad token", ex.getMessage());
    }

    @Test
    void phoneNumberExistsException() {
        PhoneNumberExistsException ex = new PhoneNumberExistsException("254701234567");
        assertTrue(ex.getMessage().contains("254701234567"));
    }

    @Test
    void phoneNumberNotFoundException() {
        PhoneNumberNotFoundException ex = new PhoneNumberNotFoundException("x");
        assertTrue(ex.getMessage().contains("x"));
    }

    @Test
    void profileCreationException() {
        ProfileCreationException ex = new ProfileCreationException("254701234567");
        assertTrue(ex.getMessage().contains("254701234567"));
    }

    @Test
    void profileNotFoundException() {
        ProfileNotFoundException ex = new ProfileNotFoundException(7L);
        assertTrue(ex.getMessage().contains("7"));
    }

    @Test
    void verifyAccountException() {
        VerifyAccountException ex = new VerifyAccountException("verify");
        assertTrue(ex.getMessage().contains("verify"));
    }
}
