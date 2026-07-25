package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventCacheProjectionTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void fixtureDeserializesAndSerializesWithTheExactV1SnakeCaseContract() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/event-cache-v1.json")) {
            EventCacheProjection projection = objectMapper.readValue(input, EventCacheProjection.class);

            assertEquals(1, projection.schemaVersion());
            assertEquals("event-provider", projection.eventId());
            assertEquals("2030-01-02T03:04:05Z", projection.eventDate().toString());
            assertEquals("Lions", projection.teams().getFirst().name());
            assertEquals(-110, projection.markets().getFirst().participants().getFirst()
                    .lines().getFirst().prices().get("9").price());

            String serialized = objectMapper.writeValueAsString(projection);
            assertTrue(serialized.contains("\"schema_version\":1"));
            assertTrue(serialized.contains("\"event_id\":\"event-provider\""));
            assertTrue(serialized.contains("\"market_description\":\"Main\""));
        }
    }

    @Test
    void projectionConvertsToGatewayEventWithoutChangingMapperInputs() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/event-cache-v1.json")) {
            EventCacheProjection projection = objectMapper.readValue(input, EventCacheProjection.class);
            Event event = projection.toEvent();

            assertEquals("event-provider", event.getEventId());
            assertEquals(1L, event.getId());
            assertEquals("Lions v Tigers", event.getEventName());
            assertEquals(2, event.getMarkets().size());
            assertEquals(1, event.getMarkets().getFirst().getParticipants().getFirst().getPrices().size());
        }
    }
}
