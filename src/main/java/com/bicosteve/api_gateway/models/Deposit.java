package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    private Long profileId;
    private String trxRef;
    private BigDecimal amount;
    private String currency;
    private String checkoutUrl;
    private Integer status;
}
