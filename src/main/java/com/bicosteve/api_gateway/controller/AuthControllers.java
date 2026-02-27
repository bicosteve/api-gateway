package com.bicosteve.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllers {

    @GetMapping("/register")
    public String registerUser(){
        return "Register user";
    }

    @GetMapping("/login")
    public String loginUser(){
        return "Login user";
    }
}
