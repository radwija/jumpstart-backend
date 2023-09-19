package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<?> showAllProducts() {
        BaseResponse<?> response = productService.showAllProducts();
        return ResponseEntity.ok(response);
    }
}
