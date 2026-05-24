package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Score response object")
public class ScoreResponse {
    @Schema(example = "1")
    private Long            id;

    @Schema(example = "2")
    private Integer         eventStatus;

    @Schema(example = "95")
    private Integer         scoreAway;

    @Schema(example = "133")
    private Integer         scoreHome;

    @Schema(example = "0")
    private Integer         gameClock;

    @Schema(example = "4")
    private Integer         gamePeriod;
}
