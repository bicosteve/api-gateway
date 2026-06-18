package com.bicosteve.api_gateway.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleProfileNotFoundReturns404() {
        ProfileNotFoundException ex = new ProfileNotFoundException(1L);
        ResponseEntity<ErrorResponse> response = handler.handleProfileNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handlePhoneNumberNotFoundReturns404() {
        ResponseEntity<ErrorResponse> response =
                handler.handlePhoneNumberNotFound(new PhoneNumberNotFoundException("254701234567"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handlePhoneNumberExistsReturns409() {
        ResponseEntity<ErrorResponse> response =
                handler.handlePhoneNumberExists(new PhoneNumberExistsException("254701234567"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleValidationErrorsReturns400() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fe = new FieldError("req", "phoneNumber", "must be valid");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fe));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals("must be valid", response.getBody().getValidationErrors().get("phoneNumber"));
    }

    @Test
    void handleProfileCreationReturns400() {
        ResponseEntity<ErrorResponse> response =
                handler.handleProfileCreationErrors(new ProfileCreationException("254701234567"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleAccountVerificationReturns400() {
        ResponseEntity<ErrorResponse> response =
                handler.handleAccountVerificationErrors(new VerifyAccountException("x"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleInvalidOtpReturns400() {
        ResponseEntity<ErrorResponse> response =
                handler.handlesInvalidOtp(new InvalidOtpException("invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleOtpExpiredReturns410() {
        ResponseEntity<ErrorResponse> response =
                handler.handlesInvalidOtp(new OtpExpiredException("expired"));

        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertEquals(410, response.getBody().getStatus());
    }

    @Test
    void handleEventNotFoundReturns404() {
        ResponseEntity<ErrorResponse> response =
                handler.handleEventNotFound(new EventNotFoundException("e1"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleGenericExceptionReturns500StatusCodeWith410BodyStatus() {
        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.GONE.value(), response.getBody().getStatus());
    }

    @Test
    void handleIllegalExceptionReturns400() {
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalException(new IllegalArgumentException("bad"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleExpiredEventReturns400() {
        ResponseEntity<ErrorResponse> response =
                handler.handleExpiredEventException(new ExpiredEventException("expired"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleBadSqlGrammarReturns500() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBadSqlGrammar(new BadSqlGrammaException("syntax"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("A database error occurred", response.getBody().getMessage());
    }

    @Test
    void handleBetNotFoundReturns404() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBetNotFound(new BetNotFoundException("bet 1"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
    }
}
