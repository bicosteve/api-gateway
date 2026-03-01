package com.bicosteve.api_gateway.controller;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.dto.requests.VerifyRequest;
import com.bicosteve.api_gateway.dto.response.ProfileDto;
import com.bicosteve.api_gateway.exceptions.PhoneNumberExistsException;
import com.bicosteve.api_gateway.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.random.RandomGenerator;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllers {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerUser(
            @Valid @RequestBody RegisterRequest request
    ){
        this.profileService.getProfileByPhoneNumber(request).ifPresent(profile -> {
            throw new PhoneNumberExistsException(request.getPhoneNumber());
        });


        SecureRandom random = new SecureRandom();
        int otp = 100_000 + random.nextInt(900_000);

        this.profileService.createProfile(request);

        Map<String,String> response = new HashMap<>();
        response.put("message","User registered successfully");
        response.put("otp",String.valueOf(otp));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-account")
    public ResponseEntity<Map<String,String>> verifyUser(@Valid @RequestBody VerifyRequest request){
        this.profileService.verifyProfile(request);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Working"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginUser(){
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","token"));
    }
}
