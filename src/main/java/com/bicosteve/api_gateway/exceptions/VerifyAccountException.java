package com.bicosteve.api_gateway.exceptions;

public class VerifyAccountException extends RuntimeException {
    public VerifyAccountException(String message){
        super("An error occurred verifying %s ".formatted(message));
    }
}
