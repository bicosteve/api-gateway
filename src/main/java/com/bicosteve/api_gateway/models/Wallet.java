package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    private Integer             id;
    private Integer             profileId;
    private BigDecimal          balance;
    private BigDecimal          bonus;
    private String              createdBy;
    private LocalDateTime       createdAt;
    private LocalDateTime       updatedAt;
}
