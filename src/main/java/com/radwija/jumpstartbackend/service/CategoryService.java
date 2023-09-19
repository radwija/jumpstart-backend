package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.CreateCategoryRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

public interface CategoryService {
    BaseResponse<?> saveCategory(String currentUserEmail, CreateCategoryRequest request);
    BaseResponse<?> showCategories();
}
