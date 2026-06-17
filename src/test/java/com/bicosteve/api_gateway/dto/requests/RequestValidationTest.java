package com.bicosteve.api_gateway.dto.requests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    // ---- RegisterRequest ----
    @Test
    void registerRequestValid() {
        RegisterRequest r = new RegisterRequest("254701234567", "a@b.com", "pass1234", "pass1234");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(r);
        assertTrue(violations.isEmpty());
    }

    @Test
    void registerRequestMissingPhone() {
        RegisterRequest r = new RegisterRequest("", "a@b.com", "pass1234", "pass1234");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    @Test
    void registerRequestBlankEmail() {
        RegisterRequest r = new RegisterRequest("254701234567", "", "pass1234", "pass1234");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    @Test
    void registerRequestPasswordMismatch() {
        RegisterRequest r = new RegisterRequest("254701234567", "a@b.com", "pass1234", "different");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    @Test
    void registerRequestBlankPassword() {
        RegisterRequest r = new RegisterRequest("254701234567", "a@b.com", "", "");
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    // ---- LoginRequest ----
    @Test
    void loginRequestValid() {
        LoginRequest r = new LoginRequest("254701234567", "pass1234");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(r);
        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequestMissingFields() {
        LoginRequest r = new LoginRequest("", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    // ---- VerifyRequest ----
    @Test
    void verifyRequestValid() {
        VerifyRequest r = new VerifyRequest("254701234567", "123456");
        Set<ConstraintViolation<VerifyRequest>> violations = validator.validate(r);
        assertTrue(violations.isEmpty());
    }

    @Test
    void verifyRequestMissingCode() {
        VerifyRequest r = new VerifyRequest("254701234567", "");
        Set<ConstraintViolation<VerifyRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    @Test
    void verifyRequestCodeTooShort() {
        VerifyRequest r = new VerifyRequest("254701234567", "12");
        Set<ConstraintViolation<VerifyRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    // ---- DepositRequest ----
    @Test
    void depositRequestValid() {
        DepositRequest r = DepositRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .email("a@b.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("254701234567")
                .build();
        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(r);
        assertTrue(violations.isEmpty());
    }

    @Test
    void depositRequestAmountTooSmall() {
        DepositRequest r = DepositRequest.builder()
                .amount(BigDecimal.valueOf(5))
                .email("a@b.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("254701234567")
                .build();
        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }

    @Test
    void depositRequestMissingFields() {
        DepositRequest r = new DepositRequest();
        Set<ConstraintViolation<DepositRequest>> violations = validator.validate(r);
        assertFalse(violations.isEmpty());
    }
}
