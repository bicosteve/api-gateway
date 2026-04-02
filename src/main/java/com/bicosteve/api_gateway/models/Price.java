package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price{
    private Integer         priceId;
    private Integer         rundownId;
    private BigDecimal      price;
    private BigDecimal      priceDelta;
    private Integer         isMainLine;
    private BigDecimal      odds;
    private Integer         participantId;
    private Integer         bookmakerId;
    private String          handicapValue;
    private String          lineId;
    private LocalDateTime   closedAt;
    private LocalDateTime   updatedAt;
    private LocalDateTime   createdAt;
}
