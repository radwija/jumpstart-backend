package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.payload.request.CreateCategoryRequest;
import com.radwija.jumpstartbackend.payload.request.ProductRequest;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatisticService statisticService;

    @PostMapping("/create-category")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
        final BaseResponse<?> response = categoryService.saveCategory(userService.getCurrentUser().getEmail(), request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(403).body(response);
    }

    @PostMapping("/add-product")
    public ResponseEntity<?> addProduct(
            @RequestParam("image") MultipartFile image,
            @RequestParam("productName") String productName,
            @RequestParam("slug") String slug,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Long stock,
            @RequestParam("weight") Double weight,
            @RequestParam("categoryId") Long categoryId) {
//        String currentUserEmail = userService.getCurrentUser().getEmail();
        String currentUserEmail = "";
        ProductRequest request = new ProductRequest();
        request.setProductName(productName);
        request.setSlug(slug);
        request.setDescription(description);
        request.setPrice(price);
        request.setStock(stock);
        request.setWeight(weight);
        request.setCategoryId(categoryId);
        final BaseResponse<?> response = productService.saveProduct(currentUserEmail, image, request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/update-product/{slug}")
    public ResponseEntity<?> updateProduct(@PathVariable("slug") String slug, MultipartFile image, @RequestBody ProductRequest request) {
        String currentUserEmail = userService.getCurrentUser().getEmail();

        Long productId;
        try {
            productId = productService.getProductDetailsBySlug(slug).getProductId();
            request.setProductId(productId);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(404).body(BaseResponse.notFound(e.getMessage()));
        }

        final BaseResponse<?> response = productService.saveProduct(currentUserEmail, image, request);

        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/delete-product/{productId}")
    public ResponseEntity<?> deleteProductByProductId(@PathVariable("productId") String productIdStr) {
        Long productId = Long.parseLong(productIdStr);
        String currentUserEmail = userService.getCurrentUser().getEmail();
        BaseResponse<?> response = productService.deleteProductByProductId(currentUserEmail, productId);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(@RequestParam(value = "filter", required = false) String filter) {
        User currentUser = userService.getCurrentUser();
        if (filter == null) {
            filter = "";
        }
        BaseResponse<?> response = orderService.getAllOrders(currentUser, filter);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/complete-order/{orderIdStr}")
    public ResponseEntity<?> completeOrder(@PathVariable(value = "orderIdStr") String orderIdStr) {
        User currentUser = userService.getCurrentUser();
        Long orderId = Long.parseLong(orderIdStr);
        BaseResponse<?> response = orderService.completeOrder(currentUser, orderId);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/cancel-order/{orderIdStr}")
    public ResponseEntity<?> cancelOrder(@PathVariable(value = "orderIdStr") String orderIdStr) {
        User currentUser = userService.getCurrentUser();
        Long orderId = Long.parseLong(orderIdStr);
        BaseResponse<?> response = orderService.cancelOrder(currentUser, orderId);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/customers")
    public ResponseEntity<?> showAllUsers() {
        User currentUser = userService.getCurrentUser();
        BaseResponse<?> response = userService.showAllUsers(currentUser);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> showAllStatistics() {
        User currentUser = userService.getCurrentUser();
        BaseResponse<?> response = statisticService.getAllStats(currentUser);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
