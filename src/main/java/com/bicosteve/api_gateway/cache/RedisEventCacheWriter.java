package com.bicosteve.api_gateway.cache;

import com.bicosteve.api_gateway.models.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@Component
public class RedisEventCacheWriter {
    private static final DefaultRedisScript<Long> WRITE_WITH_EXPIRY_BOUNDARY = new DefaultRedisScript<>(
            "if tonumber(ARGV[3]) >= tonumber(ARGV[4]) then "
                    + "redis.call('DEL', KEYS[1]); return redis.call('ZREM', KEYS[2], ARGV[2]); end; "
                    + "redis.call('SET', KEYS[1], ARGV[1], 'PXAT', ARGV[4]); "
                    + "return redis.call('ZADD', KEYS[2], ARGV[4], ARGV[2])", Long.class);

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final EventCacheProperties properties;

    public RedisEventCacheWriter(StringRedisTemplate redis, ObjectMapper objectMapper, Clock clock,
                                 EventCacheProperties properties) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.properties = properties;
    }

    public void refresh(Event event) {
        if (!properties.enabled()) return;
        try {
            execute(event.getEventId(), objectMapper.writeValueAsString(EventCacheProjection.from(event)),
                    event.getEventDate().toInstant().toEpochMilli());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize event " + event.getEventId(), e);
        }
    }

    public void remove(String eventId) {
        if (!properties.enabled()) return;
        execute(eventId, "{}", clock.millis());
    }

    private void execute(String eventId, String json, long eventMillis) {
        long nowMillis = clock.millis();
        redis.execute(WRITE_WITH_EXPIRY_BOUNDARY,
                List.of(properties.eventKey(eventId), properties.upcomingKey()), json, eventId,
                String.valueOf(nowMillis), String.valueOf(eventMillis));
    }
}
