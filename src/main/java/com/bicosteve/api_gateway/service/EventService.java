package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.response.EventDto;
import com.bicosteve.api_gateway.mappers.dtomappers.EventDtoMapper;
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
    private final EventDtoMapper eventDtoMapper;

    public List<EventDto> getEvents(int limit, int offset){
        List<Event> events = this.eventRepository.fetchEvents(limit,offset);
        log.info("EventService::events {} ",events);
        return events.stream()
                .map(this.eventDtoMapper::toDto)
                .toList();
    }
}
