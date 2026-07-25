package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.cache.CachePage;
import com.bicosteve.api_gateway.cache.RedisEventCacheReader;
import com.bicosteve.api_gateway.cache.RedisEventCacheWriter;
import com.bicosteve.api_gateway.dto.response.EventResponse;
import com.bicosteve.api_gateway.dto.response.PageResponse;
import com.bicosteve.api_gateway.exceptions.EventNotFoundException;
import com.bicosteve.api_gateway.mappers.dtomappers.EventDtoMapper;
import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final RedisEventCacheReader cacheReader;
    private final RedisEventCacheWriter cacheWriter;
    private final Clock clock;

    public EventService(EventRepository eventRepository, EventDtoMapper eventDtoMapper,
                        RedisEventCacheReader cacheReader, RedisEventCacheWriter cacheWriter, Clock clock) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.cacheReader = cacheReader;
        this.cacheWriter = cacheWriter;
        this.clock = clock;
    }

    public PageResponse<EventResponse> getEvents(int limit, int offset) {
        Optional<CachePage> cached = readPage(limit, offset);
        List<Event> events;
        boolean hasNext;
        if (cached.isPresent()) {
            events = cached.get().events();
            hasNext = cached.get().hasNext();
        } else {
            events = eventRepository.fetchEvents(limit + 1, offset);
            hasNext = events.size() > limit;
            events.forEach(this::refreshOrRemove);
        }
        List<EventResponse> data = events.stream().limit(limit).map(eventDtoMapper::toDto).toList();
        return PageResponse.<EventResponse>builder().data(data).page(offset / limit).limit(limit).hasNext(hasNext)
                .hasPrevious(offset > 0).build();
    }

    public EventResponse getEvent(String eventId) {
        Event event = readEvent(eventId).orElseGet(() -> {
            Event databaseEvent = eventRepository.fetchOneEvent(eventId);
            if (databaseEvent != null) refreshOrRemove(databaseEvent);
            return databaseEvent;
        });
        if (event == null) {
            log.warn("No event with id {} was found", eventId);
            throw new EventNotFoundException(eventId);
        }
        return eventDtoMapper.toDto(event);
    }

    private Optional<Event> readEvent(String eventId) {
        try {
            return cacheReader.find(eventId);
        } catch (RuntimeException exception) {
            log.warn("Event cache read failed for {}", eventId, exception);
            return Optional.empty();
        }
    }

    private Optional<CachePage> readPage(int limit, int offset) {
        try {
            return cacheReader.findPage(limit, offset);
        } catch (RuntimeException exception) {
            log.warn("Event cache page read failed", exception);
            return Optional.empty();
        }
    }

    private void refreshOrRemove(Event event) {
        try {
            if (event.getEventDate() != null && event.getEventDate().toInstant().toEpochMilli() > clock.millis()) {
                cacheWriter.refresh(event);
            } else if (event.getEventId() != null) {
                cacheWriter.remove(event.getEventId());
            }
        } catch (RuntimeException exception) {
            log.warn("Event cache write failed for {}", event.getEventId(), exception);
        }
    }
}
