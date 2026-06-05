package com.bicosteve.api_gateway.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Deposit response object")
public class DepositResponse {
    @Schema(example = "773331cea51...")
    private String trxRef;

    @Schema(example = "https://www.something.com")
    private String checkoutUrl;

    @Schema(example = "20.00")
    private BigDecimal amount;

    @Schema(example = "BIRR")
    private String currency;

    @Schema(example = "profileId")
    private Long profileId;
}
