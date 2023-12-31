package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/products/id/{productId}")
    public ResponseEntity<?> showProductDetailsByProductId(@PathVariable("productId") String productIdStr) {
        Long productId = Long.parseLong(productIdStr);
        BaseResponse<?> response = productService.showProductDetailsByProductId(productId);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(404).body(response);
    }

    @GetMapping("/products/slug/{slug}")
    public ResponseEntity<?> showProductDetailsBySlug(@PathVariable("slug") String slug) {
        BaseResponse<?> response = productService.showProductDetailsBySlug(slug);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(404).body(response);
    }

    @GetMapping("/products/search")
    public ResponseEntity<?> searchForProducts(
            @RequestParam(value = "q", required = false) String searchQuery,
            @RequestParam(value = "category", required = false) String categorySlug) {

        BaseResponse<?> response = productService.searchForProducts(categorySlug, searchQuery);

        return ResponseEntity.ok(response);
    }
}
