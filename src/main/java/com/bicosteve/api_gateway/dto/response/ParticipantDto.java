package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipantDto{
    private Long            participantId;
    private Integer         rundownId;
    private String          type;
    private String          name;
    private Long            marketId;

    private List<PriceDto>  prices;

}
