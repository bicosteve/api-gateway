package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bet{
    private Integer         betId;
    private String          eventId;
    private Integer         profileId;
    private BigDecimal      stake;
    private Integer         isBonus;
    private Integer         status;
    private BigDecimal      totalOdds;
    private BigDecimal      possibleWin;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;

    private List<Slip> slips;
}
