package com.bicosteve.api_gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalStatus {
    public static final int PENDING_APPROVAL    = 0;
    public static final int APPROVED            = 1;
    public static final int PROCESSING          = 2;
    public static final int COMPLETED           = 3;
    public static final int REJECTED            = 4;
    public static final int FAILED              = 5;
}
