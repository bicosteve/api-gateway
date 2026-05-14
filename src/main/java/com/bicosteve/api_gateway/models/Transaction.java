package com.bicosteve.api_gateway.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
