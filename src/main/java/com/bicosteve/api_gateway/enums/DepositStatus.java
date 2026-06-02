package com.bicosteve.api_gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositStatus {
    public static final int PENDING = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
}
