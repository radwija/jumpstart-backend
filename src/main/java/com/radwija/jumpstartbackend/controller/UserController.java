package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.ItemRequest;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductSnapshotService productSnapshotService;

    @GetMapping("/me")
    public ResponseEntity<?> userProfile() {
        return ResponseEntity.ok(userService.getCurrentUser().getUserProfile());
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.getCurrentUser();
        final BaseResponse<?> response = userProfileService.updateProfile(user, updateUserRequest);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/update-profile-picture")
    public ResponseEntity<?> updateProfile(@RequestParam("profilePicture") MultipartFile profilePicture) {
        User user = userService.getCurrentUser();
        final BaseResponse<?> response = userProfileService.updateProfilePicture(user, profilePicture);
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

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "order", required = false) String orderBy) {
        User currentUser = userService.getCurrentUser();
        if (filter == null) {
            filter = "";
        }
        if (orderBy == null) {
            orderBy = "";
        }
        BaseResponse<?> response = orderService.getMyOrders(currentUser, filter, orderBy);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/my-orders/snapshot/slug/{slug}")
    public ResponseEntity<?> showSnapshotDetailsBySlug(@PathVariable("slug") String slug) {
        User currentUser = userService.getCurrentUser();
        BaseResponse<?> response = productSnapshotService.showSnapshotDetailsBySlug(currentUser, slug);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
