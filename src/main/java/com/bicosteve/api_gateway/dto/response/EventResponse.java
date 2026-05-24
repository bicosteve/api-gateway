package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Event response object")
public class EventResponse {
    @Schema(example = "05400602aa7f38e714f7c773331cea51")
    private String                      eventId;

    @Schema(example = "3")
    private Integer                     sportId;

    @Schema(example = "2026-05-24T23:20:00")
    private OffsetDateTime              eventDate;

    @Schema(example = "Regular Season")
    private String                      seasonType;

    @Schema(example = "2026")
    private Integer                     seasonYear;

    @Schema(example = "Texas at Los Angeles - 2026-05-24T23:20:00Z")
    private String                      eventName;

    @Schema(example = "0")
    private Integer                     eventStatus;

    private List<TeamResponse>   teams =     new ArrayList<>();
    private List<MarketResponse> markets =   new ArrayList<>();
    private ScoreResponse score;

}
