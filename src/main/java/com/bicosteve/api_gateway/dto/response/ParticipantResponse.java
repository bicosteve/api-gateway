package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Participant response object")
public class ParticipantResponse {

    @Schema(example = "30828")
    private Long                    participantId;

    @Schema(example = "3436")
    private Integer                 rundownId;

    @Schema(example = "TEAM_TYPE")
    private String                  type;

    @Schema(example = "Arsenal FC")
    private String                  name;

    @Schema(example = "14190")
    private Long                    marketId;

    private List<PriceResponse>  prices = new ArrayList<>();

}
