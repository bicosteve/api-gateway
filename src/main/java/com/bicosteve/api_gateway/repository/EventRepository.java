package com.bicosteve.api_gateway.repository;


import com.bicosteve.api_gateway.models.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepository{
    private final JdbcTemplate jdbcTemplate;

    // STEP 00. Create mapper which maps flat rows into nested Event object.
    private List<Event> mapRowsToEvents(List<Map<String,Object>> rows){
        Map<String, Event> eventMap = new LinkedHashMap<>();

        for(Map<String, Object> row: rows){
            String eventId = (String) row.get("event_id");

            // Get/Create event
//            Event event = eventMap.computeIfAbsent()
        }
        return null;
    }

    //STEP 01. get paginated event_ids only
    public List<String> findEventIds(int page, int size){
        int offset = page * size;
        String sql = """
                SELECT event_id FROM rundown_event
                ORDER BY event_date DESC
                LIMIT ? OFFSET ?
                """;
        return this.jdbcTemplate.queryForList(sql,String.class,size,offset);
    }

    //STEP 02. Fetch full data for the collected event_ids
    public List<Event> findEventsByIds(List<String> eventIds){
        if(eventIds.isEmpty()) return new ArrayList<>();
        return null;
    }
}
