package com.bicosteve.api_gateway.dto.requests;

import com.bicosteve.api_gateway.validation.UniqueSlip;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Data
@UniqueSlip
public class BetRequest{
    @NotBlank(message = "Profile Id is required")
    @JsonProperty("profile_id")
    private String profileId;

    @NotBlank(message = "Stake is required")
    @JsonProperty("stake")
    private Double stake;

    @NotNull(message = "Total odds is required")
    @DecimalMin(value="1.2", inclusive = false, message = "Total odds must be greater than 1.2")
    @JsonProperty("total_odds")
    private BigDecimal totalOdds;

    @NotBlank(message = "Token is required")
    @JsonProperty("token")
    private String token;

    @NotBlank(message = "isBonus is required")
    @JsonProperty("is_bonus")
    private Integer isBonus;

    @Size(min=1,max = 10, message = "selections must be between 1 and 10")
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
}
