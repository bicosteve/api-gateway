package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScoreDto{
    private Long            id;
    private Integer         eventStatus;
    private Integer         scoreAway;
    private Integer         scoreHome;
    private Integer         gameClock;
    private Integer         gamePeriod;
}
