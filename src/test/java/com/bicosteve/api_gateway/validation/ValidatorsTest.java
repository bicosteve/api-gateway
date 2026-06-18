package com.bicosteve.api_gateway.validation;

import com.bicosteve.api_gateway.dto.requests.BetRequest;
import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.SlipRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidatorsTest {

    private ConstraintValidatorContext ctx;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        ctx = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
    }

    @Test
    void phoneNumberValidatorAcceptsValidKenyanNumbers() {
        PhoneNumberValidator v = new PhoneNumberValidator();
        ConstraintValidatorContext ctx = mock(ConstraintValidatorContext.class);
        assertTrue(v.isValid("0712345678", ctx));
        assertTrue(v.isValid("0112345678", ctx));
        assertTrue(v.isValid("+254712345678", ctx));
        assertTrue(v.isValid("254712345678", ctx));
    }

    @Test
    void phoneNumberValidatorRejectsInvalid() {
        PhoneNumberValidator v = new PhoneNumberValidator();
        assertFalse(v.isValid("123", ctx));
        assertFalse(v.isValid("abcdefg", ctx));
    }

    @Test
    void phoneNumberValidatorNullOrBlankPasses() {
        PhoneNumberValidator v = new PhoneNumberValidator();
        assertTrue(v.isValid(null, ctx));
        assertTrue(v.isValid("", ctx));
    }

    @Test
    void passwordMatchesValidatorTrueForMatch() {
        PasswordMatchesValidator v = new PasswordMatchesValidator();
        RegisterRequest r = new RegisterRequest("p", "e", "pass", "pass");
        assertTrue(v.isValid(r, ctx));
    }

    @Test
    void passwordMatchesValidatorFalseForMismatch() {
        PasswordMatchesValidator v = new PasswordMatchesValidator();
        RegisterRequest r = new RegisterRequest("p", "e", "pass1", "pass2");
        assertFalse(v.isValid(r, ctx));
    }

    @Test
    void passwordMatchesValidatorFalseForNullPassword() {
        PasswordMatchesValidator v = new PasswordMatchesValidator();
        RegisterRequest r = new RegisterRequest("p", "e", null, "x");
        assertFalse(v.isValid(r, ctx));
    }

    @Test
    void slipValidatorReturnsTrueForUnique() {
        SlipValidator v = new SlipValidator();
        BetRequest req = new BetRequest();
        req.setSlips(List.of(
                new SlipRequest("e1", 19, 1, 1, "moneyline", "Chelsea", 2.0, ""),
                new SlipRequest("e2", 19, 2, 1, "moneyline", "Arsenal", 2.0, "")
        ));
        assertTrue(v.isValid(req, ctx));
    }

    @Test
    void slipValidatorReturnsFalseForDuplicate() {
        SlipValidator v = new SlipValidator();
        BetRequest req = new BetRequest();
        req.setSlips(List.of(
                new SlipRequest("e1", 19, 1, 1, "moneyline", "Chelsea", 2.0, ""),
                new SlipRequest("e1", 19, 1, 1, "moneyline", "Arsenal", 2.0, "")
        ));
        assertFalse(v.isValid(req, ctx));
    }
}
