package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.payload.request.ProductRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

public interface ProductService {
    BaseResponse<?> saveProduct(String currentUserEmail, ProductRequest productRequest);
    Product mapProductRequestToNewProduct(ProductRequest productRequest);
    void mapProductRequestToExistingProduct(ProductRequest productRequest, Product existingProduct);
    BaseResponse<?> showAllProducts();
    BaseResponse<?> showProductDetailsByProductId(Long productId);
    BaseResponse<?> showProductDetailsBySlug(String slug);
    String handleUniqueSlug(Product product, String slug);
}
