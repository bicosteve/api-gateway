package com.bicosteve.api_gateway.exceptions;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(String message){
        super(message);
    }
}
