package com.bicosteve.api_gateway.exceptions;

public class PhoneNumberExistsException extends RuntimeException {
    public PhoneNumberExistsException(String phoneNumber){
        super("The phone number %s already exists".formatted(phoneNumber));
    }
}
