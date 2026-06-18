package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.UniqueSlip;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SlipRequestTest {

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

    @Test
    void specialBetValueValidForMoneyline() {
        SlipRequest slip = new SlipRequest("e1", 19, 1, 1, "moneyline", "Chelsea", 2.0, "");
        assertTrue(slip.isSpecialBetValueValid());
    }

    @Test
    void specialBetValueValidForHandicapWithValue() {
        SlipRequest slip = new SlipRequest("e1", 19, 1, 2, "handicap", "Chelsea", 2.0, "hcp=2.5");
        assertTrue(slip.isSpecialBetValueValid());
    }

    @Test
    void specialBetValueInvalidForHandicapWithEmpty() {
        SlipRequest slip = new SlipRequest("e1", 19, 1, 2, "handicap", "Chelsea", 2.0, "");
        assertFalse(slip.isSpecialBetValueValid());
    }

    @Test
    void specialBetValueValidForTotalsWithValue() {
        SlipRequest slip = new SlipRequest("e1", 19, 1, 3, "totals", "Over", 2.0, "over=2.5");
        assertTrue(slip.isSpecialBetValueValid());
    }

    @Test
    void specialBetValueInvalidForTotalsWithNull() {
        SlipRequest slip = new SlipRequest("e1", 19, 1, 3, "totals", "Over", 2.0, null);
        assertFalse(slip.isSpecialBetValueValid());
    }

    @Test
    void specialBetValueValidWhenMarketNameNull() {
        SlipRequest slip = new SlipRequest("e1", 19, 1, 1, null, "Chelsea", 2.0, null);
        assertTrue(slip.isSpecialBetValueValid());
    }

    @Test
    void uniqueSlipAnnotationOnBetRequest() {
        assertNotNull(BetRequest.class.getAnnotation(UniqueSlip.class));
    }

    @Test
    void validationFailsWhenEventIdMissing() {
        SlipRequest slip = new SlipRequest("", 19, 1, 1, "moneyline", "Chelsea", 2.0, "");
        Set<ConstraintViolation<SlipRequest>> violations = validator.validate(slip);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("event id is required")));
    }
}
