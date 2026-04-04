package com.bicosteve.api_gateway.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlipRequest{
    @JsonProperty("event_id")
    @NotBlank(message = "event id is required")
    private String eventId;

    @JsonProperty("sport_id")
    @NotNull(message = "sport_id is required")
    private Integer sportId;

    @JsonProperty("team_id")
    @NotNull(message = "Team id is required")
    private Integer teamId;

    @JsonProperty("market_id")
    @NotNull(message = "market id is required")
    private Integer marketId; // comes from the participant field

    @JsonProperty("market_name")
    @NotBlank(message = "market name is required")
    private String marketName;

    @JsonProperty("participant_name") // team_name else draw
    @NotBlank(message = "participant_name is required")
    private String participantName;

    @JsonProperty("odds")
    @NotNull(message = "odds is required")
    private Double odds; // comes from participant > prices odds field

}
