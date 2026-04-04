package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventDto{
    private String                      eventId;
    private Integer                     sportId;
    private LocalDateTime               eventDate;
    private String                      seasonType;
    private Integer                     seasonYear;
    private String                      eventName;
    private Integer                     eventStatus;

    private List<TeamDto>   teams =     new ArrayList<>();
    private List<MarketDto> markets =   new ArrayList<>();
    private ScoreDto                    score;

}
