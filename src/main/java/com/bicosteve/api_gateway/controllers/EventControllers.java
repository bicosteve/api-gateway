package com.bicosteve.api_gateway.controllers;


import com.bicosteve.api_gateway.dto.response.EventDto;
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
    public ResponseEntity<Map<String, List<EventDto>>> showAllEvents(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset){

        var events = this.eventService.getEvents(limit,offset);

        Map<String, List<EventDto>> allEvents = Map.of("events",events);

        return  ResponseEntity.status(HttpStatus.OK).body(allEvents);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> showAnEvent(@PathVariable String eventId){
        EventDto event = this.eventService.getEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(event);
    }
}
