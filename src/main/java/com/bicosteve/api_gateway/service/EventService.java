package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService{
    private final EventRepository eventRepository;

    public List<Event> getEvents(int limit,int offset){
        List<Event> events = this.eventRepository.fetchEvents(limit,offset);
        log.info("EventService::events {} ",events);
        return events;
    }
}
