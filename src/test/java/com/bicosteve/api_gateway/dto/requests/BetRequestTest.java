package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.UniqueSlip;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BetRequestTest {

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

    private SlipRequest slip(String eventId, int teamId, String marketName, double odds) {
        return new SlipRequest(eventId, 19, teamId, 1, marketName, "Chelsea", odds, "");
    }

    @Test
    void calculateTotalOddsWithMultipleSlips() {
        BetRequest req = new BetRequest();
        req.setSlips(List.of(
                slip("e1", 1, "moneyline", 2.0),
                slip("e2", 2, "moneyline", 3.0)
        ));

        req.calculateTotalOdds();

        assertNotNull(req.getTotalOdds());
        assertEquals(0, BigDecimal.valueOf(6.0).compareTo(req.getTotalOdds()));
    }

    @Test
    void calculateTotalOddsWithNullSlipsLeavesTotalOddsNull() {
        BetRequest req = new BetRequest();
        req.calculateTotalOdds();
        assertNull(req.getTotalOdds());
    }

    @Test
    void calculateTotalOddsWithEmptySlipsLeavesTotalOddsNull() {
        BetRequest req = new BetRequest();
        req.setSlips(List.of());
        req.calculateTotalOdds();
        assertNull(req.getTotalOdds());
    }

    @Test
    void hasDuplicateEventReturnsTrueForDuplicates() {
        BetRequest req = new BetRequest();
        req.setSlips(List.of(
                slip("e1", 1, "moneyline", 2.0),
                slip("e1", 2, "moneyline", 3.0)
        ));
        assertTrue(req.hasDuplicateEvent());
    }

    @Test
    void hasDuplicateEventReturnsFalseForUnique() {
        BetRequest req = new BetRequest();
        req.setSlips(List.of(
                slip("e1", 1, "moneyline", 2.0),
                slip("e2", 2, "moneyline", 3.0)
        ));
        assertFalse(req.hasDuplicateEvent());
    }

    @Test
    void hasUniqueSlipAnnotation() {
        assertNotNull(BetRequest.class.getAnnotation(UniqueSlip.class));
    }

    @Test
    void validationFailsWhenStakeMissing() {
        BetRequest req = new BetRequest();
        req.setIsBonus(0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 2.0)));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Stake is required")));
    }

    @Test
    void validationFailsWhenIsBonusMissing() {
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 2.0)));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("isBonus is required")));
    }

    @Test
    void validationFailsWhenStakeBelowMinimum() {
        BetRequest req = new BetRequest();
        req.setStake(19.99);
        req.setIsBonus(0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 2.0)));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Stake must be at least 20.0")));
    }

    @Test
    void validationPassesWhenStakeAtMinimum() {
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setIsBonus(0);
        req.setSlips(List.of(slip("e1", 1, "moneyline", 2.0)));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getMessage().contains("Stake")));
    }

    @Test
    void validationFailsWhenSlipsEmpty() {
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setIsBonus(0);
        req.setSlips(List.of());
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("selections must be between 1 and 10")));
    }

    @Test
    void validationFailsWhenSlipsNull() {
        // Regression: previously @Size ignored null, letting a null slips list through to an NPE
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setIsBonus(0);
        // slips left null
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("selections are required")));
    }

    @Test
    void validationCascadesToNestedSlipWhenEventIdBlank() {
        // Regression: without @Valid, nested SlipRequest constraints were never enforced
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setIsBonus(0);
        req.setSlips(List.of(new SlipRequest("", 19, 1, 1, "moneyline", "Chelsea", 2.0, "")));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("event id is required")));
    }

    @Test
    void validationCascadesToNestedSlipWhenOddsNull() {
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setIsBonus(0);
        req.setSlips(List.of(new SlipRequest("e1", 19, 1, 1, "moneyline", "Chelsea", null, "")));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("odds is required")));
    }

    @Test
    void validationCascadesToNestedSlipSpecialBetValueRule() {
        // handicap market requires a non-blank special_bet_value (@AssertTrue on SlipRequest)
        BetRequest req = new BetRequest();
        req.setStake(20.0);
        req.setIsBonus(0);
        req.setSlips(List.of(new SlipRequest("e1", 19, 1, 2, "handicap", "Chelsea", 2.0, "")));
        Set<ConstraintViolation<BetRequest>> violations = validator.validate(req);
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("special_bet_value is required for this market")));
    }
}
