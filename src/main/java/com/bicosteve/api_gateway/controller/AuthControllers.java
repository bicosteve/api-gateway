package com.bicosteve.api_gateway.controller;

import com.bicosteve.api_gateway.dto.requests.LoginRequest;
import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.service.ProfileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllers {
    private final ProfileService profileService;
    private final HttpServletResponse response;


    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerUser(
            @Valid @RequestBody RegisterRequest request
    ){
        Map<String,String> response = this.profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-account")
    public ResponseEntity<Map<String,String>> verifyUser(
            @Valid @RequestBody VerifyRequest request
    ){
        Map<String,String> response = this.profileService.verifyProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginUser(
            @Valid @RequestBody LoginRequest request
            ){
        Map<String,String> tokens = this.profileService.generateLoginToken(request,this.response);
        return ResponseEntity.status(HttpStatus.OK).body(tokens);
    }
}
