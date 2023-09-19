package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.Category;
import com.radwija.jumpstartbackend.exception.RefusedActionException;
import com.radwija.jumpstartbackend.payload.request.CreateCategoryRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CategoryRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public BaseResponse<?> saveCategory(String currentUserEmail, CreateCategoryRequest request) {
        BaseResponse<Category> response = new BaseResponse<>();
        try {
            if (!userRepository.existsByEmailAndRole(currentUserEmail, ERole.ROLE_ADMIN)) {
                throw new RefusedActionException("Forbidden");
            }
            Category newCategory = new Category();
            newCategory.setCategoryName(request.getCategoryName());
            String slug = request.getCategoryName().toLowerCase().trim().replaceAll(" ", "_");
            newCategory.setCategorySlug(slug);
            categoryRepository.save(newCategory);

            response.setCode(200);
            response.setMessage("New category created successfully!");
            response.setResult(newCategory);
            return response;
        } catch (RefusedActionException e) {
            response.setCode(403);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public BaseResponse<?> showCategories() {
        List<Category> categories = categoryRepository.findAll();

        return BaseResponse.ok(categories);
    }

    @Override
    public BaseResponse<?> showCategories(String orderBy) {
        List<Category> categories = new ArrayList<>();
        switch (orderBy) {
            case "asc":
                categories = categoryRepository.findAllByOrderByCategoryNameAsc();
                break;
            case "desc":
                categories = categoryRepository.findAllByOrderByCategoryNameDesc();
                break;
            default:
                categories = categoryRepository.findAll();
        }

        return BaseResponse.ok(categories);
    }

    @Override
    public BaseResponse<?> showCategoriesWithProducts(String orderBy) {
        List<Category> categories = new ArrayList<>();
        switch (orderBy) {
            case "asc":
                categories = categoryRepository.findAllByOrderByCategoryNameAsc();
                break;
            case "desc":
                categories = categoryRepository.findAllByOrderByCategoryNameDesc();
                break;
            default:
                categories = categoryRepository.findAll();
        }

        return BaseResponse.ok(categories);
    }
}
