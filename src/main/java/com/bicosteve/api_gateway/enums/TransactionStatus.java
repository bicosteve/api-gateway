package com.bicosteve.api_gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    PENDING(0),
    SUCCESS(1),
    FAILED(3),
    REVERSED(4),
    WONBET(5),
    VOIDBET(7),
    OTHER(8);

    private final int status;
}
