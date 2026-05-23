package com.bicosteve.api_gateway.controllers;


import com.bicosteve.api_gateway.dto.response.EventResponse;
import com.bicosteve.api_gateway.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventControllers{

    private final EventService eventService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<EventResponse>>> showAllEvents(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset){

        var events = this.eventService.getEvents(limit,offset);

        Map<String, List<EventResponse>> allEvents = Map.of("events",events);

        return  ResponseEntity.status(HttpStatus.OK).body(allEvents);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> showAnEvent(@PathVariable String eventId){
        EventResponse event = this.eventService.getEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(event);
    }
}
