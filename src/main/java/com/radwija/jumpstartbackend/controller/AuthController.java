package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserRegisterRequest request) {
        userService.saveUser(request);
        return "Saved successfully!";
    }
}
