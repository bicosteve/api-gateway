package com.bicosteve.api_gateway.exceptions;

public class BetNotFoundException extends RuntimeException {
    public BetNotFoundException(String message){
        super("Bet with bet id %s not found".formatted(message));
    }
}
