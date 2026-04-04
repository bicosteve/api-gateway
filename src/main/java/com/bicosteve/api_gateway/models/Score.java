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
public class Score{
    private Long            id;
    private String          eventId;
    private Integer         eventStatus;
    private String          eventStatusDetail;
    private Integer         teamIdAway;
    private Integer         teamIdHome;
    private Integer         winnerAway;
    private Integer         winnerHome;
    private Integer         scoreAway;
    private Integer         scoreHome;
    private Integer         gameClock;
    private Integer         gamePeriod;
    private String          broadcast;
    private String          venueName;
    private String          venueLocation;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
}
