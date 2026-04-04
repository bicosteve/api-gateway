package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team{
    private Long            id;
    private Long            teamId;
    private String          eventId;
    private String          name;
    private String          mascot;
    private String          abbreviation;
    private int             isHome;
    private int             isAway;
    private String          record;
    private Integer         conferenceId;
    private Integer         divisionId;
    private Integer         ranking;
    private String          leagueName;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
}
