package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.payload.request.CreateCategoryRequest;
import com.radwija.jumpstartbackend.payload.request.ProductRequest;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.CategoryService;
import com.radwija.jumpstartbackend.service.ProductService;
import com.radwija.jumpstartbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @PostMapping("/create-category")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
        final BaseResponse<?> response = categoryService.saveCategory(userService.getCurrentUser().getEmail(), request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(403).body(response);
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest request) {
        String currentUserEmail = userService.getCurrentUser().getEmail();
        final BaseResponse<?> response = productService.saveProduct(currentUserEmail, request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/update-product/{slug}")
    public ResponseEntity<?> updateProduct(@PathVariable("slug") String slug, @RequestBody ProductRequest request) {
        String currentUserEmail = userService.getCurrentUser().getEmail();

        Long productId;
        try {
            productId = productService.getProductDetailsBySlug(slug).getProductId();
            request.setProductId(productId);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(404).body(BaseResponse.notFound(e.getMessage()));
        }

        final BaseResponse<?> response = productService.saveProduct(currentUserEmail, request);

        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
