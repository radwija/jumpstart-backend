package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.Category;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.exception.CategoryNotFoundException;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.exception.RefusedActionException;
import com.radwija.jumpstartbackend.payload.request.ProductRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CategoryRepository;
import com.radwija.jumpstartbackend.repository.ProductRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.ProductService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public BaseResponse<?> saveProduct(String currentUserEmail, ProductRequest productRequest) {
        BaseResponse<Product> response = new BaseResponse<>();
        try {
            boolean isAdmin = userRepository.existsByEmailAndRole(currentUserEmail, ERole.ROLE_ADMIN);
            if (!isAdmin) {
                throw new RefusedActionException("Forbidden");
            }
            System.out.println("category id service: " + productRequest.getCategoryId());
            Category category = categoryRepository.findByCategoryId(productRequest.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            String rawSlug = productRequest.getSlug().toLowerCase().trim().replaceAll(" ", "-");

            if (productRequest.getProductId() == null) {
                Product newProduct = mapProductRequestToNewProduct(productRequest);
                newProduct.setCategory(category);
                newProduct.setSlug(handleUniqueSlug(newProduct, rawSlug));
                newProduct.setCreatedAt(new Date());
                productRepository.save(newProduct);

                response.setCode(200);
                response.setMessage("New product added successfully!");
                response.setResult(newProduct);
            } else {
                Product existingProduct = productRepository.findByProductId(productRequest.getProductId()).orElseThrow(() -> new ProductNotFoundException("Product not found"));
                mapProductRequestToExistingProduct(productRequest, existingProduct);
                existingProduct.setSlug(handleUniqueSlug(existingProduct, rawSlug));
                existingProduct.setUpdatedAt(new Date());
                productRepository.save(existingProduct);

                response.setCode(200);
                response.setMessage("Product ID: " + existingProduct.getProductId() + " updated successfully!");
                response.setResult(existingProduct);
            }

            return response;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    @Override
    public Product mapProductRequestToNewProduct(ProductRequest productRequest) {
        Product product = new Product();
        BeanUtils.copyProperties(productRequest, product);
        return product;
    }

    @Override
    public void mapProductRequestToExistingProduct(ProductRequest productRequest, Product existingProduct) {
        Date createdAt = existingProduct.getCreatedAt();
        BeanUtils.copyProperties(productRequest, existingProduct);
        existingProduct.setCreatedAt(createdAt);
    }

    @Override
    public BaseResponse<?> showAllProducts() {
        List<Product> products = productRepository.findAll();
        return BaseResponse.ok(products);
    }

    @Override
    public BaseResponse<?> showProductDetailsByProductId(Long productId) {
        try {
            Product detailedProduct = productRepository.findByProductId(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
            return BaseResponse.ok(detailedProduct);
        } catch (ProductNotFoundException e) {
            return BaseResponse.notFound(e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> showProductDetailsBySlug(String slug) {
        try {
            Product detailedProduct = productRepository.findBySlug(slug);
            if (detailedProduct == null) {
                throw new ProductNotFoundException("Product not found");
            }
            return BaseResponse.ok(detailedProduct);
        } catch (ProductNotFoundException e) {
            return BaseResponse.notFound(e.getMessage());
        }
    }

    @Override
    public Product getProductDetailsBySlug(String slug) {
        Product product = productRepository.findBySlug(slug);
        if (product == null) {
            throw new ProductNotFoundException("Product not found");
        }
        return product;
    }

    @Override
    public String handleUniqueSlug(Product product, String slug) {
        boolean isSlugTaken = productRepository.existsBySlug(slug);

        if (!isSlugTaken) {
            return slug;
        }

        if (productRepository.findBySlug(slug).equals(product)) {
            return slug;
        }

        while (isSlugTaken) {
            int underscoreIndex = slug.lastIndexOf("_");

            if (underscoreIndex >= 0) {
                slug = slug.substring(0, underscoreIndex);
                slug += "_" + RandomString.make(16);
            }
            isSlugTaken = productRepository.existsBySlug(slug);
        }

        return slug;
    }
}
