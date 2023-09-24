package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.request.ItemRequest;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.ItemService;
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

    @Autowired
    private ItemService itemService;

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

    @PostMapping("/add-product-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody ItemRequest request) {
        String currentUserEmail = userService.getCurrentUser().getEmail();
        final BaseResponse<?> response = itemService.saveCartItem(currentUserEmail, request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/set-item-quantity")
    public ResponseEntity<?> setItemQuantity(@RequestBody ItemRequest request) {
        String currentUserEmail = userService.getCurrentUser().getEmail();
        final BaseResponse<?> response = itemService.saveCartItem(currentUserEmail, request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/get-my-cart")
    public ResponseEntity<?> getMyCart() {
        String currentUserEmail = userService.getCurrentUser().getEmail();
        final BaseResponse<?> response = cartService.getMyCart(currentUserEmail);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/delete-cart-item/{cartItemId}")
    public ResponseEntity<?> deleteProductByProductId(@PathVariable("cartItemId") String cartItemIdStr) {
        Long cartItemId = Long.parseLong(cartItemIdStr);
        String currentUserEmail = userService.getCurrentUser().getEmail();
        BaseResponse<?> response = itemService.deleteCartItemById(currentUserEmail, cartItemId);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
