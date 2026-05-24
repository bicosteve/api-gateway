package com.bicosteve.api_gateway.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Price response object")
public class PriceResponse {
    @Schema(example = "250720")
    private Integer             priceId;
//    private Integer             rundownId;

    @Schema(example = "10.0")
    private BigDecimal          odds;

    @Schema(example = "30080")
    private Integer             participantId;

    @Schema(example = "137.5")
    private String              handicapValue;

    @Schema(example = "0d78cbf9ea3028eb4dec7033072d28d6")
    private String              lineId;

    @Schema(example = "2026-05-08T13:00:12Z")
    private OffsetDateTime      closedAt;
}
