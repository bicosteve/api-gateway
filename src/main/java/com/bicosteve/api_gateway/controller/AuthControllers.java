package com.bicosteve.api_gateway.controller;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllers {

    private final ProfileService profileService;

    @GetMapping("/register")
    public String registerUser(@Valid @RequestBody RegisterRequest request){
        var profile = this.profileService.getProfileByEmail(request);
        if(profile == null){
            return "Profile not found";
        }
        return profile.get().getPhoneNumber();
    }

    @GetMapping("/login")
    public String loginUser(){
        return "Login user";
    }
}
