package com.bicosteve.api_gateway.exceptions;

public class PhoneNumberNotFoundException extends RuntimeException {
    public PhoneNumberNotFoundException(String message){
        super("Phone number %s not found".formatted(message));
    }
}
