package com.bicosteve.api_gateway.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Slip{
    private Integer             betSlipId;
    private Integer             betId;
    private String              eventId;
    private Integer             sportId;
    private Integer             teamId;
    private Integer             marketId;
    private String              marketName;
    private String              participantName;
    private BigDecimal          odds;
    private LocalDateTime       createdAt;
    private LocalDateTime       updatedAt;

}
