package com.bicosteve.api_gateway.exceptions;

public class BadSqlGrammaException extends RuntimeException {
    public BadSqlGrammaException(String message){
        super("Bad sql grammar %s ".formatted(message));
    }
}
