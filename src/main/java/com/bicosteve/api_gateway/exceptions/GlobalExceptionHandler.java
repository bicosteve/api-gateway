package com.bicosteve.api_gateway.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String getCurrentTimestamp(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    // 404 - ProfileNotFound
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProfileNotFound(
            ProfileNotFoundException ex
    ){
        // construct error message
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    // 404 - PhoneNumberNotFound
    @ExceptionHandler(PhoneNumberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNumberNotFound(
            PhoneNumberNotFoundException ex
    ){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );
        return new ResponseEntity <>(error,HttpStatus.NOT_FOUND);
    }


    // 400 - PhoneNumberAlreadyExists
    @ExceptionHandler(PhoneNumberExistsException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNumberExists(
            PhoneNumberExistsException ex
    ){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 400 - Validation errors (@Valid Failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex
    ){
        // 00. Initialize errors map
        Map<String, String> errors = new HashMap<>();

        // 01. Extract clean messages from each field
        ex.getBindingResult().getFieldErrors().forEach(
                err -> errors.put(err.getField(),err.getDefaultMessage()));

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Valiation failed")
                .timestamp(this.getCurrentTimestamp())
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 400 - Profile Creation Error
    @ExceptionHandler(ProfileCreationException.class)
    public ResponseEntity<ErrorResponse> handleProfileCreationErrors(
            ProfileCreationException ex
    ){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity <>(error, HttpStatus.BAD_REQUEST);
    }

    // 400 - Profile Verification Error
    @ExceptionHandler(VerifyAccountException.class)
    public ResponseEntity<ErrorResponse> handleAccountVerificationErrors(
            VerifyAccountException ex)
    {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handlesInvalidOtp(
            InvalidOtpException ex)
    {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> handlesInvalidOtp(
            OtpExpiredException ex)
    {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<> (error, HttpStatus.GONE);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(
            EventNotFoundException ex
    ){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }



    // 500 - Catch any other unhandled exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex
    ){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

