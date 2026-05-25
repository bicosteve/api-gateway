package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Slip response object")
public class SlipResponse {
    @Schema(example = "1")
    private Long            betSlipId;

    @Schema(example = "1")
    private Long            betId;

    @Schema(example = "9c0dc8eb5c35ff4fe656d5013f27f4f9")
    private String          eventId;

    @Schema(example = "19")
    private Integer         sportId;

    @Schema(example = "5432")
    private Integer         teamId;

    @Schema(example = "1")
    private Integer         marketId;

    @Schema(example = "handicap")
    private String          marketName;

    @Schema(example = "Chelsea")
    private String          participantName;

    @Schema(example = "1.50")
    private BigDecimal      odds;

    @Schema(example = "hcp=2.5")
    private String          specialBetValue;

    @Schema(example = "3")
    private Integer          status;

    @Schema(example = "2026-05-22T04:40:56")
    private LocalDateTime   createdAt;

    @Schema(example = "2026-05-22T04:40:56")
    private LocalDateTime   updatedAt;
}
