package com.bicosteve.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private OffsetDateTime  closedAt;
    private LocalDateTime   updatedAt;
    private LocalDateTime   createdAt;
}
