package com.bicosteve.api_gateway.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Individual bet slip selection")
public class SlipRequest{
    @JsonProperty("event_id")
    @NotBlank(message = "event id is required")
    @Schema(description = "Rundown event ID", example = "2defa5f34847147c70718b2fc6d2fa9a")
    private String eventId;

    @JsonProperty("sport_id")
    @NotNull(message = "sport_id is required")
    @Schema(description = "Rundown sport ID", example = "19")
    private Integer sportId;

    @JsonProperty("team_id")
    @NotNull(message = "Team id is required")
    @Schema(description = "Rundown team ID", example = "3456")
    private Integer teamId;

    @JsonProperty("market_id")
    @NotNull(message = "market id is required")
    @Schema(description = "Rundown market type. 1=moneyline,2=handicap,3=totals", example = "1")
    private Integer marketId; // comes from the participant field

    @JsonProperty("market_name")
    @NotBlank(message = "market name is required")
    @Schema(description = "Rundown market name.", example = "moneyline")
    private String marketName;

    @JsonProperty("participant_name") // team_name else draw
    @NotBlank(message = "participant_name is required")
    @Schema(description = "Team name or selection", example = "Chelsea")
    private String participantName;

    @JsonProperty("odds")
    @NotNull(message = "odds is required")
    @Schema(description = "Decimal participant/team's odds at place of placing bets", example = "5.50")
    private Double odds; // comes from participant > prices odds field


    @JsonProperty("special_bet_value")
    @NotNull(message = "special_bet_value is required")
    @Schema(
            description = "Special bet value. For handicap use hcp=2.5 or hcp=-2.5. For totals use over=2.5 or under=2.5. Empty for moneyline",
            example = "hcp=2.5")
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
