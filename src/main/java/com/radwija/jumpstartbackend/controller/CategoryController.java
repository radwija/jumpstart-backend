package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.entity.Category;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<?> showAllCategories(@RequestParam(value = "order", required = false) String orderBy) {
        BaseResponse<?> response = categoryService.showCategories();
        if (orderBy != null) {
            response = categoryService.showCategories(orderBy);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/with-products")
    public ResponseEntity<?> showAllCategoriesWithProducts(@RequestParam(value = "order", required = false) String orderBy) {
        BaseResponse<?> response = categoryService.showCategories();
        if (orderBy != null) {
            response = categoryService.showCategories(orderBy);
        }
        return ResponseEntity.ok(response);
    }
}
