package com.bicosteve.api_gateway.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
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


    @JsonProperty("special_bet_value")
    @NotNull(message = "special_bet_value is required")
    private String specialBetValue = "";

    @AssertTrue(message = "special_bet_value is required for this market")
    @JsonIgnore
    public boolean isSpecialBetValueValid(){
        // 01. Handle null safety for marketName
        if(marketName == null) return true;

        String market = marketName.toLowerCase();

        // 02. Check if the market is a special market
        boolean isSpecialMarket = market.contains("handicap") || market.contains("totals");

        // 03. If special market, MUST NOT be null or ""
        if(isSpecialMarket){
            return specialBetValue != null && !specialBetValue.isBlank();
        }

        return true;
    }
    
}
