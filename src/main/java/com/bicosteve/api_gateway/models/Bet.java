package com.bicosteve.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bet{
    private Integer         betId;
    private Long            profileId;
    private BigDecimal      stake;
    private Integer         isBonus;
    private Integer         status;
    private BigDecimal      totalOdds;
    private BigDecimal      possibleWin;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;

    private List<Slip> slips;
}
