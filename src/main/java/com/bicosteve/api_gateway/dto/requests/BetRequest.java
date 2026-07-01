package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.UniqueSlip;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@UniqueSlip
@AllArgsConstructor
@NoArgsConstructor
public class BetRequest{
    @JsonIgnore
    private Long profileId;

    @NotNull(message = "Stake is required")
    @DecimalMin(value="20.0", message = "Stake must be at least 20.0")
    @JsonProperty("stake")
    @Schema(example = "200.0")
    private Double stake;

    @JsonIgnore
    private BigDecimal totalOdds;

    @NotNull(message = "isBonus is required")
    @JsonProperty("is_bonus")
    @Schema(example = "0")
    private Integer isBonus;

    @NotNull(message = "selections are required")
    @Valid
    @Size(min=1,max = 10, message = "selections must be between 1 and 10")
    @Schema(description = "List of bet selections, minimum 1 and maximum 10",
            minLength = 1,
            maxLength = 10,
            example = """
            [
              {
                "event_id": "2defa5f34847147c70718b2fc6d2fa9a",
                                "sport_id": 19,
                "team_id": 3916,
                "market_id": 1,
                "market_name": "moneyline",
                "participant_name":"Bayern Munich",
                "odds": 1.13,
                "special_bet_value": ""
              },
              {
                "event_id": "9c0dc8eb5c35ff4fe656d5013f27f4f6",
                                "sport_id": 19,
                "team_id": 1234,
                "market_id": 2,
                "market_name": "handicap",
                "participant_name":"Bayern Munich",
                "odds": 2.91,
                "special_bet_value": "hcp=-2.5"
              },
              {
                "event_id": "9c0dc8eb5c35ff4fe656d5013f27f4f9",
                                "sport_id": 19,
                "team_id": 9,
                "market_id": 3,
                "market_name": "totals",
                "participant_name":"Under",
                "odds": 3.10,
                "special_bet_value": "totals=2.5"
              }
            ]
            """)
    private List<SlipRequest> slips;

    public void calculateTotalOdds(){
        if(this.slips != null && !this.slips.isEmpty()){
            double product = 1.0;
            for(SlipRequest slip : this.slips){
                if(slip.getOdds() != null){
                    product *= slip.getOdds();
                }
            }

            this.totalOdds = BigDecimal.valueOf(product);
        }
    }

    public boolean hasDuplicateEvent(){
        Set<String> eventIds = new HashSet<>();
        for(SlipRequest slip : slips){
            if(!eventIds.add(slip.getEventId())){
                return true;
            }
        }
        return false;
    }

}
