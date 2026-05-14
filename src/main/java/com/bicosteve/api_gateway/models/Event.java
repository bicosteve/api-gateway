package com.bicosteve.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event{
    private Long            id;
    private String          eventId;
    private String          eventUuid;
    private Integer         sportId;
    private OffsetDateTime  eventDate;
    private String          seasonType;
    private Integer         seasonYear;
    private String          eventName;
    private String          eventHeadline;
    private Integer         eventStatus;
    private LocalDateTime   createdAt;
    private LocalDateTime   updateAt;

    private List<Team>      teams;
    private List<Market>    markets;
    private Score           score;
}
