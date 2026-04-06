package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BetDto{
    private Integer     betId;
    private String     profiledId;
    private Double      stake;
    private Double      possibleWin;
    private int         isBonus;
}
