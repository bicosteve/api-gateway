package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.UniqueSlip;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
    @DecimalMin(value="2.0", message = "Stake must be greater than 2.0")
    @JsonProperty("stake")
    @Schema(example = "2.0")
    private Double stake;

    @JsonIgnore
    private BigDecimal totalOdds;

    @NotNull(message = "isBonus is required")
    @JsonProperty("is_bonus")
    @Schema(example = "0")
    private Integer isBonus;

    @Size(min=1,max = 10, message = "selections must be between 1 and 10")
    @Schema(description = "List of bet selections, minimum 1 and maximum 10",
            minLength = 1,
            maxLength = 10,
            example = """
            [
              {
                "eventId": "2defa5f34847147c70718b2fc6d2fa9a",
                "teamId": 11196,
                "marketId": 1,
                "marketName": "moneyline",
                "odds": 3.50,
                "specialBetValue": ""
              },
              {
                "eventId": "9c0dc8eb5c35ff4fe656d5013f27f4f6",
                "teamId": 1234,
                "marketId": 2,
                "marketName": "handicap",
                "odds": 2.91,
                "specialBetValue": "hcp=2.5"
              },
              {
                "eventId": "9c0dc8eb5c35ff4fe656d5013f27f4f9",
                "teamId": 9,
                "marketId": 3,
                "marketName": "totals",
                "odds": 1.91,
                "specialBetValue": "totals=2.5"
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
