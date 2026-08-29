package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RedisEventCacheReaderTest {
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> values = mock(ValueOperations.class);
    private final ZSetOperations<String, String> zsets = mock(ZSetOperations.class);
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final Clock clock = Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC);
    private final EventCacheProperties properties = new EventCacheProperties(true, "event-cache", "v1");
    private final RedisEventCacheWriter writer = mock(RedisEventCacheWriter.class);
    private RedisEventCacheReader reader;

    @BeforeEach
    void setUp() {
        when(redis.opsForValue()).thenReturn(values);
        when(redis.opsForZSet()).thenReturn(zsets);
        reader = new RedisEventCacheReader(redis, mapper, clock, properties, writer);
    }

    @Test
    void detailHitReturnsProjection() throws Exception {
        when(values.get("event-cache:v1:event:e1")).thenReturn(json(event("e1", 2)));

        assertEquals("e1", reader.find("e1").orElseThrow().getEventId());
    }

    @Test
    void detailMissReturnsEmpty() {
        when(values.get(anyString())).thenReturn(null);
        assertTrue(reader.find("missing").isEmpty());
    }

    @Test
    void pastDetailIsRemovedWithTheAtomicWriter() throws Exception {
        when(values.get("event-cache:v1:event:e1")).thenReturn(json(event("e1", 1)));

        assertTrue(reader.find("e1").isEmpty());
        verify(writer).remove("e1");
    }

    @Test
    void fullListPagePreservesIndexOrderOffsetAndHasNextUsingMultiGet() throws Exception {
        when(zsets.rangeByScore(eq("event-cache:v1:upcoming"), eq(1893456000000d),
                eq(Double.POSITIVE_INFINITY), eq(5L), eq(3L)))
                .thenReturn(new java.util.LinkedHashSet<>(List.of("e2", "e3", "e4")));
        when(values.multiGet(List.of("event-cache:v1:event:e2", "event-cache:v1:event:e3", "event-cache:v1:event:e4")))
                .thenReturn(List.of(json(event("e2", 2)), json(event("e3", 3)), json(event("e4", 4))));

        CachePage page = reader.findPage(2, 5).orElseThrow();

        assertEquals(List.of("e2", "e3"), page.events().stream().map(Event::getEventId).toList());
        assertTrue(page.hasNext());
        verify(zsets).rangeByScore(eq("event-cache:v1:upcoming"), eq(1893456000000d),
                eq(Double.POSITIVE_INFINITY), eq(5L), eq(3L));
    }

    @Test
    void incompleteCandidateDetailsCannotServePage() throws Exception {
        when(zsets.rangeByScore(anyString(), anyDouble(), anyDouble(), anyLong(), anyLong())).thenReturn(Set.of("e1", "e2"));
        when(values.multiGet(anyList())).thenReturn(java.util.Arrays.asList(json(event("e1", 1)), null));

        assertTrue(reader.findPage(2, 0).isEmpty());
        verify(writer).remove(anyString());
    }

    private String json(Event event) throws Exception {
        return mapper.writeValueAsString(EventCacheProjection.from(event));
    }

    private Event event(String id, int day) {
        return Event.builder().id((long) day).eventId(id).eventDate(OffsetDateTime.parse("2030-01-0" + day + "T00:00:00Z"))
                .teams(List.of()).markets(List.of()).build();
    }
}
