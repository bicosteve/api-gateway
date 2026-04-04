package com.bicosteve.api_gateway.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse{
    private int status;
    private String message;
    private String timestamp;
    private Map<String,String> validationErrors;
}
