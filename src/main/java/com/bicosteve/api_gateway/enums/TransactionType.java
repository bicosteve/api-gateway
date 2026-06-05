package com.bicosteve.api_gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    CREDIT(1),
    DEBIT(2);

    private final int status;
}
