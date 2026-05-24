package com.bicosteve.api_gateway.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Team response object")
public class TeamResponse {
    @Schema(example = "1")
    private Long            id;

    @Schema(example = "60")
    private Long            teamId;

    @Schema(example = "Middlesex")
    private String          name;

    @Schema(example = "1")
    private int             isHome;

    @Schema(example = "0")
    private int             isAway;

    @Schema(example = "American League")
    private String          leagueName;
}
