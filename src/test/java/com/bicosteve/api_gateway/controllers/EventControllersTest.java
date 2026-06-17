package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.response.EventResponse;
import com.bicosteve.api_gateway.dto.response.PageResponse;
import com.bicosteve.api_gateway.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventControllersTest {

    private EventService eventService;
    private EventControllers controller;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        controller = new EventControllers(eventService);
    }

    @Test
    void showAllEventsReturnsOk() {
        PageResponse<EventResponse> page = PageResponse.<EventResponse>builder()
                .data(List.of(new EventResponse()))
                .page(0).limit(10).hasNext(false).hasPrevious(false)
                .build();
        when(eventService.getEvents(10, 0)).thenReturn(page);

        ResponseEntity<PageResponse<EventResponse>> r = controller.showAllEvents(10, 0);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().getData().size());
    }

    @Test
    void showAnEventReturnsOk() {
        EventResponse e = new EventResponse();
        e.setEventId("e1");
        when(eventService.getEvent("e1")).thenReturn(e);

        ResponseEntity<EventResponse> r = controller.showAnEvent("e1");
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("e1", r.getBody().getEventId());
    }
}
