package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipantResponse {
    private Long                    participantId;
    private Integer                 rundownId;
    private String                  type;
    private String                  name;
    private Long                    marketId;

    private List<PriceResponse>  prices = new ArrayList<>();

}
