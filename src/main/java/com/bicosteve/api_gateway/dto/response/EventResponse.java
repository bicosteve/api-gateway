package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventResponse {
    private String                      eventId;
    private Integer                     sportId;
    private OffsetDateTime              eventDate;
    private String                      seasonType;
    private Integer                     seasonYear;
    private String                      eventName;
    private Integer                     eventStatus;

    private List<TeamResponse>   teams =     new ArrayList<>();
    private List<MarketResponse> markets =   new ArrayList<>();
    private ScoreResponse score;

}
