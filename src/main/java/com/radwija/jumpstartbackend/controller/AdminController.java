package com.radwija.jumpstartbackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @GetMapping("/anjing")
    public String mantap() {
        return "mantap";
    }

}
