package com.bicosteve.api_gateway.dto.response;

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
public class BetDto{
    private Integer                 betId;
    private Integer                 profiledId;
    private BigDecimal              stake;
    private BigDecimal              possibleWin;
    private int                     isBonus;
    private int                     status;
    private BigDecimal              totalOdds;
    private LocalDateTime           createdAt;

    private List<SlipDto> slips =   new ArrayList<>();

}
