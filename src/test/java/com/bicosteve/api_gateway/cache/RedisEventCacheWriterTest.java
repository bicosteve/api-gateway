package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RedisEventCacheWriterTest {
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final Clock clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00.123Z"), ZoneOffset.UTC);
    private final EventCacheProperties properties = new EventCacheProperties(true, "event-cache", "v1");
    private final RedisEventCacheWriter writer = new RedisEventCacheWriter(
            redis, new ObjectMapper().findAndRegisterModules(), clock, properties);

    @Test
    void refreshPassesAtomicLuaKeysAndExpiryBoundaryArguments() {
        writer.refresh(event("2030-01-02T00:00:00.456Z"));

        ArgumentCaptor<RedisScript<Long>> script = scriptCaptor();
        verify(redis).execute(script.capture(),
                eq(List.of("event-cache:v1:event:event-1", "event-cache:v1:upcoming")),
                anyString(), eq("event-1"), eq("1893456000123"), eq("1893542400456"));
        String lua = script.getValue().getScriptAsString();
        assertTrue(lua.contains("tonumber(ARGV[3]) >= tonumber(ARGV[4])"));
        assertTrue(lua.contains("DEL"));
        assertTrue(lua.contains("ZREM"));
        assertTrue(lua.contains("PXAT"));
        assertTrue(lua.contains("ZADD"));
    }

    @Test
    void eventAtNowUsesAtomicDeletionBranch() {
        writer.refresh(event("2030-01-01T00:00:00.123Z"));

        verify(redis).execute(org.mockito.ArgumentMatchers.<RedisScript<Long>>any(),
                eq(List.of("event-cache:v1:event:event-1", "event-cache:v1:upcoming")),
                anyString(), eq("event-1"), eq("1893456000123"), eq("1893456000123"));
    }

    @Test
    void removeUsesSameAtomicWriterWithPastBoundary() {
        writer.remove("event-1");

        verify(redis).execute(org.mockito.ArgumentMatchers.<RedisScript<Long>>any(),
                eq(List.of("event-cache:v1:event:event-1", "event-cache:v1:upcoming")),
                eq("{}"), eq("event-1"), eq("1893456000123"), eq("1893456000123"));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ArgumentCaptor<RedisScript<Long>> scriptCaptor() {
        return (ArgumentCaptor) ArgumentCaptor.forClass(RedisScript.class);
    }

    private Event event(String date) {
        return Event.builder().eventId("event-1").eventDate(OffsetDateTime.parse(date)).build();
    }
}
