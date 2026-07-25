package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class RedisEventCacheReader {
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final EventCacheProperties properties;
    private final RedisEventCacheWriter writer;

    public RedisEventCacheReader(StringRedisTemplate redis, ObjectMapper objectMapper, Clock clock,
                                 EventCacheProperties properties, RedisEventCacheWriter writer) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.properties = properties;
        this.writer = writer;
    }

    public Optional<Event> find(String eventId) {
        if (!properties.enabled()) return Optional.empty();
        try {
            String json = redis.opsForValue().get(properties.eventKey(eventId));
            if (json == null) return Optional.empty();
            Event event = objectMapper.readValue(json, EventCacheProjection.class).toEvent();
            if (event.getEventDate() == null || event.getEventDate().toInstant().toEpochMilli() <= clock.millis()) {
                repair(eventId);
                return Optional.empty();
            }
            return Optional.of(event);
        } catch (RuntimeException | JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public Optional<CachePage> findPage(int limit, int offset) {
        if (!properties.enabled()) return Optional.empty();
        try {
            Set<String> ids = redis.opsForZSet().rangeByScore(properties.upcomingKey(), clock.millis(),
                    Double.POSITIVE_INFINITY, offset, limit + 1L);
            if (ids == null) return Optional.empty();
            List<String> orderedIds = List.copyOf(ids);
            List<String> jsons = redis.opsForValue().multiGet(orderedIds.stream().map(properties::eventKey).toList());
            if (jsons == null || jsons.size() != orderedIds.size()) return Optional.empty();
            for (int index = 0; index < orderedIds.size(); index++) {
                if (jsons.get(index) == null) {
                    repair(orderedIds.get(index));
                    return Optional.empty();
                }
            }
            List<Event> events = jsons.stream().map(this::deserialize).toList();
            for (int index = 0; index < events.size(); index++) {
                Event event = events.get(index);
                if (!orderedIds.get(index).equals(event.getEventId()) || event.getEventDate() == null
                        || event.getEventDate().toInstant().toEpochMilli() <= clock.millis()) {
                    repair(orderedIds.get(index));
                    return Optional.empty();
                }
            }
            return Optional.of(new CachePage(events, events.size() > limit));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private void repair(String eventId) {
        try {
            writer.remove(eventId);
        } catch (RuntimeException ignored) {
            // Cache repair is best effort; the caller will use MySQL for the whole page.
        }
    }

    private Event deserialize(String json) {
        try {
            return objectMapper.readValue(json, EventCacheProjection.class).toEvent();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid cached event", e);
        }
    }
}
