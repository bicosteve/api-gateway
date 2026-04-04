package com.bicosteve.api_gateway.exceptions;

public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(String message){
        super("Event with id %s not found".formatted(message));
    }
}
