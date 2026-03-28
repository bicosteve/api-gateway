package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event{
    private Long            id;
    private String          eventId;
    private String          eventUuid;
    private Integer         sportId;
    private LocalDateTime   eventDate;
    private String          seasonType;
    private Integer         seasonYear;
    private String          eventName;
    private String          eventHeadline;
    private Integer         eventStatus;
    private LocalDateTime   createdAt;
    private LocalDateTime   updateAt;
}
