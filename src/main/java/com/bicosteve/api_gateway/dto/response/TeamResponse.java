package com.bicosteve.api_gateway.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamResponse {
    private Long            id;
    private Long            teamId;
    private String          name;
    private int             isHome;
    private int             isAway;
    private String          leagueName;
}
