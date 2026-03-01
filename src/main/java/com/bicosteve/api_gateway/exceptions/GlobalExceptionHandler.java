package com.bicosteve.api_gateway.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - ProfileNotFound
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleProfileNotFound(
            ProfileNotFoundException ex
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",ex.getMessage()));
    }

    // 404 - PhoneNumberNotFound
    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<Map<String,String>> handlePhoneNumberNotFound(
            PhoneNumberNotFoundException ex
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",ex.getMessage()));
    }


    // 400 - PhoneNumberAlreadyExists
    @ExceptionHandler(PhoneNumberExistsException.class)
    public ResponseEntity<Map<String,String>> handlePhoneNumberExists(
            PhoneNumberExistsException ex
    ){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",ex.getMessage()));
    }

    // 400 - Validation errors (@Valid Failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(err -> errors.put(err.getField(),err.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // 400 - Profile Creation Error
    @ExceptionHandler(ProfileCreationException.class)
    public ResponseEntity<Map<String,String>> handleProfileCreationErrors(
            ProfileCreationException ex
    ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",ex.getMessage()));
    }

    // 400 - Profile Verification Error
    @ExceptionHandler(VerifyAccountException.class)
    public ResponseEntity<Map<String,String>> handleAccountVerificationErrors(
            VerifyAccountException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("messsage",ex.getMessage()));
    }



    // 500 - Catch any other unhandled exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleGenericException(
            Exception ex
    ){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","An unexpected error occurred"));
    }
}

