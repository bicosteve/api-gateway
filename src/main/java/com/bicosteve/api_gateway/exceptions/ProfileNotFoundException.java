package com.bicosteve.api_gateway.exceptions;

public class ProfileNotFoundException extends RuntimeException{
    public ProfileNotFoundException(Long id){
        super("Profile with id %d not found".formatted(id));
    }
}
