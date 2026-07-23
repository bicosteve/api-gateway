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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {
    private EventRepository repository;
    private RedisEventCacheReader reader;
    private RedisEventCacheWriter writer;
    private EventService service;

    @BeforeEach
    void setUp() {
        repository = mock(EventRepository.class);
        reader = mock(RedisEventCacheReader.class);
        writer = mock(RedisEventCacheWriter.class);
        service = new EventService(repository, new EventDtoMapper(), reader, writer,
                Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    void detailHitDoesNotQueryMySql() {
        when(reader.find("e1")).thenReturn(Optional.of(event("e1", 2)));
        assertEquals("e1", service.getEvent("e1").getEventId());
        verifyNoInteractions(repository);
    }

    @Test
    void detailMissFallsBackAndRefillsFutureEvent() {
        when(reader.find("e1")).thenReturn(Optional.empty());
        when(repository.fetchOneEvent("e1")).thenReturn(event("e1", 2));
        assertEquals("e1", service.getEvent("e1").getEventId());
        verify(writer).refresh(any(Event.class));
    }

    @Test
    void detailCacheErrorFallsBackAndNotFoundKeepsExistingException() {
        when(reader.find("missing")).thenThrow(new IllegalStateException("redis unavailable"));
        when(repository.fetchOneEvent("missing")).thenReturn(null);
        assertThrows(EventNotFoundException.class, () -> service.getEvent("missing"));
    }

    @Test
    void listCacheFullPagePreservesHasNextAndAvoidsMySql() {
        when(reader.findPage(2, 3)).thenReturn(Optional.of(new CachePage(List.of(event("e4", 2), event("e5", 3), event("e6", 4)), true)));
        PageResponse<EventResponse> page = service.getEvents(2, 3);
        assertEquals(List.of("e4", "e5"), page.getData().stream().map(EventResponse::getEventId).toList());
        assertTrue(page.isHasNext());
        assertTrue(page.isHasPrevious());
        verifyNoInteractions(repository);
    }

    @Test
    void incompleteListFallsBackOnceToMySqlAndRefillsAllFutureEvents() {
        when(reader.findPage(2, 0)).thenReturn(Optional.empty());
        when(repository.fetchEvents(3, 0)).thenReturn(List.of(event("e1", 2), event("e2", 3), event("e3", 4)));
        PageResponse<EventResponse> page = service.getEvents(2, 0);
        assertTrue(page.isHasNext());
        verify(repository).fetchEvents(3, 0);
        verify(writer, times(3)).refresh(any(Event.class));
    }

    @Test
    void redisListErrorFallsBackToMySql() {
        when(reader.findPage(2, 0)).thenThrow(new IllegalStateException("redis unavailable"));
        when(repository.fetchEvents(3, 0)).thenReturn(List.of(event("e1", 2)));
        assertEquals(1, service.getEvents(2, 0).getData().size());
    }

    @Test
    void pastDatabaseEventIsRemovedRatherThanCached() {
        when(reader.find("e1")).thenReturn(Optional.empty());
        when(repository.fetchOneEvent("e1")).thenReturn(event("e1", 0));
        service.getEvent("e1");
        verify(writer).remove("e1");
        verify(writer, never()).refresh(any());
    }

    @Test
    void mapperKeepsFirstParticipantPriceOnly() {
        Event event = event("e1", 2);
        com.bicosteve.api_gateway.models.Price first = com.bicosteve.api_gateway.models.Price.builder().priceId(1).build();
        com.bicosteve.api_gateway.models.Price second = com.bicosteve.api_gateway.models.Price.builder().priceId(2).build();
        com.bicosteve.api_gateway.models.Participant participant = com.bicosteve.api_gateway.models.Participant.builder()
                .participantId(1L).prices(List.of(first, second)).build();
        event.setMarkets(List.of(com.bicosteve.api_gateway.models.Market.builder().participants(List.of(participant)).build()));
        when(reader.find("e1")).thenReturn(Optional.of(event));
        assertEquals(1, service.getEvent("e1").getMarkets().getFirst().getParticipants().getFirst().getPrices().getFirst().getPriceId());
    }

    private Event event(String id, int day) {
        OffsetDateTime eventDate = day == 0 ? OffsetDateTime.parse("2029-12-31T23:59:59Z")
                : OffsetDateTime.parse("2030-01-0" + day + "T00:00:00Z");
        return Event.builder().eventId(id).sportId(19).eventDate(eventDate).eventName("Match " + id)
                .teams(new ArrayList<>()).markets(new ArrayList<>()).build();
    }
}
