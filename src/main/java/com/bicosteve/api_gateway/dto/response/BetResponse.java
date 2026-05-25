package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Bet response object")
public class BetResponse {
    @Schema(example="1")
    private Long                 betId;

    @Schema(example="2")
    private Long                    profileId;

    @Schema(example="50.0")
    private BigDecimal              stake;

    @Schema(example="500.00")
    private BigDecimal              possibleWin;

    @Schema(example="0")
    private int                     isBonus;

    @Schema(example="1")
    private int                     status;

    @Schema(example="5.5")
    private BigDecimal              totalOdds;

    @Schema(example="2026-05-22T04:40:56")
    private LocalDateTime           createdAt;

    private List<SlipResponse> slips =   new ArrayList<>();

}
