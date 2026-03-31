package com.bicosteve.api_gateway.controllers;


import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventControllers{

    private final EventService eventService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<Event>>> showAllEvents(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset){

        var events = this.eventService.getEvents(limit,offset);

        Map<String, List<Event>> allEvents = Map.of("events",events);

        return  ResponseEntity.status(HttpStatus.OK).body(allEvents);
    }
}
