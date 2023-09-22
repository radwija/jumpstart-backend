package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.CartService;
import com.radwija.jumpstartbackend.service.UserProfileService;
import com.radwija.jumpstartbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private CartService cartService;

    @GetMapping("/me")
    public ResponseEntity<?> userProfile() {
        return ResponseEntity.ok(userService.getCurrentUser().getUserProfile());
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserRequest updateUserRequest) {
        String currentUserEmail = userService.getCurrentUser().getEmail();
        final BaseResponse<?> response = userProfileService.updateProfile(currentUserEmail, updateUserRequest);

        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/add-to-cart/{productId}")
    public ResponseEntity<?> addToCart(@PathVariable("productId") String productIdStr) {
        Long productId = Long.parseLong(productIdStr);
        String currentUserEmail = userService.getCurrentUser().getEmail();
        final BaseResponse<?> response = cartService.addProductToCart(currentUserEmail, productId);

        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
