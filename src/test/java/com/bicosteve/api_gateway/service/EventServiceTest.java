package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.response.EventResponse;
import com.bicosteve.api_gateway.dto.response.PageResponse;
import com.bicosteve.api_gateway.exceptions.EventNotFoundException;
import com.bicosteve.api_gateway.mappers.dtomappers.EventDtoMapper;
import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventRepository eventRepository;
    private EventDtoMapper eventDtoMapper;
    private EventService service;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventDtoMapper = new EventDtoMapper();
        service = new EventService(eventRepository, eventDtoMapper);
    }

    private Event event(String id) {
        return Event.builder()
                .eventId(id)
                .sportId(19)
                .eventDate(OffsetDateTime.now().plusDays(1))
                .eventName("Match " + id)
                .build();
    }

    @Test
    void getEventsReturnsPage() {
        when(eventRepository.fetchEvents(11, 0))
                .thenReturn(List.of(event("e1"), event("e2")));

        PageResponse<EventResponse> page = service.getEvents(10, 0);

        assertEquals(2, page.getData().size());
        assertEquals(0, page.getPage());
        assertEquals(10, page.getLimit());
        assertFalse(page.isHasNext());
    }

    @Test
    void getEventsDetectsHasNext() {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 4; i++) events.add(event("e" + i));
        when(eventRepository.fetchEvents(11, 0)).thenReturn(events);

        PageResponse<EventResponse> page = service.getEvents(10, 0);

        assertEquals(10, page.getData().size());
        assertTrue(page.isHasNext());
    }

    @Test
    void getEventsPageComputedFromOffset() {
        when(eventRepository.fetchEvents(6, 20))
                .thenReturn(List.of(event("e1")));

        PageResponse<EventResponse> page = service.getEvents(5, 20);

        assertEquals(4, page.getPage());
        assertTrue(page.isHasPrevious());
    }

    @Test
    void getEventReturnsResponse() {
        when(eventRepository.fetchOneEvent("e1")).thenReturn(event("e1"));
        EventResponse r = service.getEvent("e1");
        assertEquals("e1", r.getEventId());
    }

    @Test
    void getEventThrowsWhenNotFound() {
        when(eventRepository.fetchOneEvent("missing")).thenReturn(null);
        assertThrows(EventNotFoundException.class, () -> service.getEvent("missing"));
    }
}
