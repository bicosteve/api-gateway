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
@Schema(description = "Market response object")
public class MarketResponse {
    @Schema(example = "4003941")
    private Integer                             marketRundownId;

    @Schema(example = "1")
    private Integer                             marketTypeId;

    @Schema(example = "0")
    private Integer                             periodId;

    @Schema(example = "moneyline")
    private String                              name;

    private List<ParticipantResponse> participants = new ArrayList<>();

}
