package com.bicosteve.api_gateway.controllers;

import com.bicosteve.api_gateway.dto.requests.LoginRequest;
import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.dto.response.*;
import com.bicosteve.api_gateway.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name="Auth Controller", description = "User Management Endpoint")
public class AuthControllers {
    private final ProfileService profileService;
    private final HttpServletResponse response;


    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user and return verification code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExistsResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<RegisterResponse> registerUser(
            @Valid @RequestBody RegisterRequest request
    ){
        RegisterResponse response = this.profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PostMapping("/verify-account")
    @Operation(
            summary = "Verify account",
            description = "Verify a user account and activate the account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account verification success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VerificationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Account verification failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<VerificationResponse> verifyUser(
            @Valid @RequestBody VerifyRequest request
    ){
        VerificationResponse response = this.profileService.verifyProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Generate access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access and refresh tokens generated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User with phone number not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotFoundResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BadRequestResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServerErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request
            ){
        LoginResponse tokens = this.profileService.generateLoginToken(request,this.response);
        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }




}
