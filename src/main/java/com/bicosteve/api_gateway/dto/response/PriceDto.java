package com.bicosteve.api_gateway.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PriceDto{
    private Integer             priceId;
    private Integer             rundownId;
    private BigDecimal          odds;
    private Integer             participantId;
    private String              handicapValue;
    private String              lineId;
    private LocalDateTime       closedAt;
}
