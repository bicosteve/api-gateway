package com.bicosteve.api_gateway.exceptions;

public class IllegalArgumentException extends RuntimeException{
    public IllegalArgumentException(String message){
        super(message);
    }
}
