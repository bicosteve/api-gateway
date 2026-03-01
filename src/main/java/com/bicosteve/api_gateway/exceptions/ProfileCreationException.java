package com.bicosteve.api_gateway.exceptions;

public class ProfileCreationException extends RuntimeException {
    public ProfileCreationException(String message){
        super("Problem creating profile for %s".formatted(message));
    }
}
