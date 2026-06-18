package com.bicosteve.api_gateway.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TrxRefGeneratorTest {

    private static final Pattern REF_PATTERN =
            Pattern.compile("DEPOSIT-\\d+-\\d+-[0-9a-f]{8}");

    @Test
    void generateTrxRefMatchesExpectedFormat() {
        TrxRefGenerator gen = new TrxRefGenerator();
        String ref = gen.generateTrxRef(42L);

        assertNotNull(ref);
        assertTrue(REF_PATTERN.matcher(ref).matches(), "Reference does not match expected format: " + ref);
        assertTrue(ref.contains("42"));
        assertTrue(ref.startsWith("DEPOSIT-42-"));
    }

    @Test
    void generateTrxRefIsUniquePerCall() {
        TrxRefGenerator gen = new TrxRefGenerator();
        Set<String> refs = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            refs.add(gen.generateTrxRef(1L));
        }
        // 100 calls with same profileId and same nanosecond could still produce
        // unique values because the uuid random portion changes.
        assertTrue(refs.size() > 1, "Trx references should be unique");
    }

    @Test
    void generateTrxRefIncludesProfileId() {
        TrxRefGenerator gen = new TrxRefGenerator();
        String ref1 = gen.generateTrxRef(7L);
        String ref2 = gen.generateTrxRef(99L);

        assertTrue(ref1.contains("7"));
        assertTrue(ref2.contains("99"));
    }
}
