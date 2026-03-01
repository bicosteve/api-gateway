package com.bicosteve.api_gateway.controller;

import com.bicosteve.api_gateway.dto.requests.RegisterRequest;
import com.bicosteve.api_gateway.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthControllers {

    private final ProfileService profileService;

    @GetMapping("/test")
    public String testWorks(){
        return "This is working. Test";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody RegisterRequest request){
        var profile = this.profileService.getProfileByEmail(request);
        if(profile == null){
            return "ProfileDto not found";
        }
        return profile.get().getPhoneNumber();
    }

    @PostMapping("/login")
    public String loginUser(){
        return "Login user";
    }
}
