package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.dto.response.EventResponse;
import com.bicosteve.api_gateway.dto.response.PageResponse;
import com.bicosteve.api_gateway.exceptions.EventNotFoundException;
import com.bicosteve.api_gateway.mappers.dtomappers.EventDtoMapper;
import com.bicosteve.api_gateway.models.Event;
import com.bicosteve.api_gateway.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService{
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;

    public PageResponse<EventResponse> getEvents(int limit, int offset){
        List<Event> events = this.eventRepository.fetchEvents(limit+1,offset);

        boolean hasNext = events.size() > limit;

        List<EventResponse> data = events.stream()
                .limit(limit)
                .map(this.eventDtoMapper::toDto)
                .toList();

        int page = offset/limit;

        log.info("Fetched {} events page={} hasNext={}"
                ,data.size()
                ,page,
                hasNext
        );

        return PageResponse.<EventResponse>builder()
                .data(data)
                .page(page)
                .limit(limit)
                .hasNext(hasNext)
                .hasPrevious(offset > 0)
                .build();
    }


    public EventResponse getEvent(String eventId){
        Event event = this.eventRepository.fetchOneEvent(eventId);
        if(event == null){
            log.warn("No event with id {} was found", eventId);
            throw new EventNotFoundException(eventId);
        }

        return this.eventDtoMapper.toDto(event);
    }
}
