package com.bicosteve.api_gateway.exceptions;


import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GlobalExceptionHandler {

    private String getCurrentTimestamp(){
        return LocalDateTime
                .now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
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
                .message("Validation failed")
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

    // 400 - Expired OTP (client must request a fresh code)
    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> handleExpiredOtp(
            OtpExpiredException ex)
    {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
        // Log the real cause server-side, but do not leak internal details to the client.
        log.error("Unhandled exception", ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalException(
            IllegalArgumentException e
    ){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredEventException.class)
    public ResponseEntity<ErrorResponse> handleExpiredEventException(ExpiredEventException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadSqlGrammaException.class)
    public ResponseEntity<ErrorResponse> handleBadSqlGrammar(BadSqlGrammaException ex){
        log.error("Bad sql grammar {}",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("A database error occurred")
                        .timestamp(this.getCurrentTimestamp())
                        .build());
    }


    // 401 - Invalid / missing / expired refresh token
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }


    // 401 - Bad credentials (wrong password during login)
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid phone number or password",
                this.getCurrentTimestamp(),
                null
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(BetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBetNotFound(BetNotFoundException ex){
        log.error("Bet not found {}",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(ex.getMessage())
                        .timestamp(this.getCurrentTimestamp())
                        .build());
    }


}

