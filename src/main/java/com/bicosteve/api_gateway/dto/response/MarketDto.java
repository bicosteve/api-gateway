package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarketDto{
    private Integer         marketRundownId;
    private Integer         marketTypeId;
    private Integer         periodId;
    private String          name;

    private List<ParticipantDto> participants;

}
