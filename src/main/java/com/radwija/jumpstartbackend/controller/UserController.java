package com.radwija.jumpstartbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/test")
    public  String test() {
        return "mantap " + new Date();
    }
}
