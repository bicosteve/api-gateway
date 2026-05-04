package com.bicosteve.api_gateway.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Integer             id;
    private Integer             profileId;
    private String              reference;
    private Integer             type;
    private BigDecimal          amount;
    private Integer             status;
    private String              createdBy;
    private LocalDateTime       createdAt;
    private LocalDateTime       updatedAt;
}
