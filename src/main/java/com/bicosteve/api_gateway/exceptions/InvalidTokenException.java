package com.bicosteve.api_gateway.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message){
        super(message);
    }
}
