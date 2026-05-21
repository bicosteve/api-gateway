package com.bicosteve.api_gateway.exceptions;

public class ExpiredEventException extends RuntimeException{
    public ExpiredEventException(String message){
        super("Event with id %s is expired".formatted(message));
    }
}
