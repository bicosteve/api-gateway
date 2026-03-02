package com.bicosteve.api_gateway.exceptions;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message){
        super(message);
    }
}
