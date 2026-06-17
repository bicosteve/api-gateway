package com.bicosteve.api_gateway.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

class LogContextTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void setAllContextWithBothValues() {
        LogContext.set("trace-123", "100");

        assertEquals("trace-123", LogContext.getTraceId());
        assertEquals("profile::100", MDC.get("profileId"));
    }

    @Test
    void setAllContextDefaultsWhenNullsProvided() {
        LogContext.set(null, null);

        assertEquals("no-trace", LogContext.getTraceId());
        assertEquals("unknown profile", MDC.get("profileId"));
    }

    @Test
    void setTraceIdStoresValue() {
        LogContext.setTraceId("abc");
        assertEquals("abc", LogContext.getTraceId());
    }

    @Test
    void setTraceIdDefaultsWhenNull() {
        LogContext.setTraceId(null);
        assertEquals("no-trace", LogContext.getTraceId());
    }

    @Test
    void setProfileIdStoresPrefixedValue() {
        LogContext.setProfileId("5");
        assertEquals("profile::5", MDC.get("profileId"));
    }

    @Test
    void setProfileIdDefaultsWhenNull() {
        LogContext.setProfileId(null);
        assertEquals("unknown profile", MDC.get("profileId"));
    }

    @Test
    void setPhoneStoresValue() {
        LogContext.setPhone("254701234567");
        assertEquals("254701234567", MDC.get("phone"));
    }

    @Test
    void setPhoneDefaultsWhenNull() {
        LogContext.setPhone(null);
        assertEquals("Unknown", MDC.get("phone"));
    }

    @Test
    void clearRemovesAllContext() {
        LogContext.set("t", "1");
        LogContext.setPhone("123");
        LogContext.clear();
        assertNull(LogContext.getTraceId());
        assertNull(MDC.get("profileId"));
        assertNull(MDC.get("phone"));
    }

    @Test
    void getTraceIdReturnsNullWhenNotSet() {
        assertNull(LogContext.getTraceId());
    }
}
