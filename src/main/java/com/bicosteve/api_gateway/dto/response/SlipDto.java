package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SlipDto{
    private Long            betSlipId;
    private Long            betId;
    private String          eventId;
    private Integer         sportId;
    private Integer         teamId;
    private Integer         marketId;
    private String          marketName;
    private String          participantName;
    private BigDecimal      odds;
    private String          specialBetValue;
    private String          status;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
}
