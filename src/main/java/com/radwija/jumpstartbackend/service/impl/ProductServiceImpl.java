package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.EItemStatus;
import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.Category;
import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.ProductSnapshot;
import com.radwija.jumpstartbackend.exception.CategoryNotFoundException;
import com.radwija.jumpstartbackend.exception.ProductNotFoundException;
import com.radwija.jumpstartbackend.exception.RefusedActionException;
import com.radwija.jumpstartbackend.payload.request.ProductRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.payload.response.ProductSearchResultDto;
import com.radwija.jumpstartbackend.repository.*;
import com.radwija.jumpstartbackend.service.ProductService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductSnapshotRepository productSnapshotRepository;

    @Override
    public void checkUserIsAdmin(String currentUserEmail) {
        boolean isAdmin = userRepository.existsByEmailAndRole(currentUserEmail, ERole.ROLE_ADMIN);
        if (!isAdmin) {
            throw new RefusedActionException("Forbidden");
        }
    }

    @Override
    public BaseResponse<?> saveProduct(String currentUserEmail, MultipartFile image, ProductRequest productRequest) {
        BaseResponse<Product> response = new BaseResponse<>();
        try {
//            checkUserIsAdmin(currentUserEmail);
            System.out.println("category id service: " + productRequest.getCategoryId());
            Category category = categoryRepository.findByCategoryId(productRequest.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            String rawSlug = productRequest.getSlug().toLowerCase().trim().replaceAll(" ", "-");

            if (image.getSize() > 304857) {
                return BaseResponse.badRequest("File size exceeds the allowed limit. File must be under 300 KB.");
            }

            if (productRequest.getProductId() == null) {
                Product newProduct = mapProductRequestToNewProduct(productRequest);
                newProduct.setCategory(category);
                newProduct.setSlug(handleUniqueSlug(newProduct, rawSlug));
                newProduct.setCreatedAt(new Date());
                byte[] productImage = image.getBytes();
                newProduct.setImage(productImage);
                productRepository.save(newProduct);

                response.setCode(200);
                response.setMessage("New product added successfully!");
                response.setResult(newProduct);
            } else {
                Product existingProduct = productRepository.findByProductId(productRequest.getProductId()).orElseThrow(() -> new ProductNotFoundException("Product not found"));
                mapProductRequestToExistingProduct(productRequest, existingProduct);
                existingProduct.setSlug(handleUniqueSlug(existingProduct, rawSlug));
                existingProduct.setUpdatedAt(new Date());
                byte[] productImage = image.getBytes();
                existingProduct.setImage(productImage);

                productRepository.save(existingProduct);

                response.setCode(200);
                response.setMessage("Product ID: " + existingProduct.getProductId() + " updated successfully!");
                response.setResult(existingProduct);
            }

            return response;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return BaseResponse.badRequest(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return BaseResponse.badRequest("Something wrong with the image");
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
                throw new ProductNotFoundException("Product not found.");
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
            throw new ProductNotFoundException("Product not found.");
        }
        return product;
    }

    @Override
    public String handleUniqueSlug(Product product, String slug) {
        boolean isSlugTaken = productRepository.existsBySlug(slug);

        if (!isSlugTaken) {
            return slug;
        }

        if (productRepository.findBySlug(slug) == product) {
            return slug;
        }

        while (isSlugTaken) {

            if (slug.contains("_")) {
                int underscoreIndex = slug.lastIndexOf("_");

                if (underscoreIndex >= 0) {
                    slug = slug.substring(0, underscoreIndex);
                    slug += "_" + RandomString.make(16);
                }
            } else {
                slug += "_" + RandomString.make(16);
            }

            isSlugTaken = productRepository.existsBySlug(slug);
        }

        return slug;
    }

    @Override
    public BaseResponse<?> deleteProductByProductId(String currentUserEmail, Long productId) {
        BaseResponse<Product> response = new BaseResponse<>();
        try {
            checkUserIsAdmin(currentUserEmail);
            if (!productRepository.existsByProductId(productId)) {
                throw new ProductNotFoundException("Product not found.");
            }
            Product product = productRepository.findByProductId(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found."));

            List<Item> purchasedItems = itemRepository.findAllByProductAndStatus(product, EItemStatus.PURCHASED);
            List<ProductSnapshot> snapshots = productSnapshotRepository.findAllByProduct(product);
            for (Item item : purchasedItems) {
                item.setProduct(null);
                itemRepository.save(item);
            }

            for (ProductSnapshot snapshot : snapshots) {
                snapshot.setProduct(null);
                productSnapshotRepository.save(snapshot);
            }

            productRepository.deleteById(productId);
            return BaseResponse.ok("Product ID: " + productId + " deleted successfully.");
        } catch (RuntimeException e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> searchForProducts(String categorySlug, String query) {
        List<Product> searchResults = productRepository.findAll();
        ProductSearchResultDto result = new ProductSearchResultDto();

        if (categorySlug != null && query != null) {
            // Search by both category and query
            searchResults = searchByCategorySlugAndQuery(categorySlug, query);
            result.setSearchResults(searchResults);
        } else if (categorySlug != null) {
            // Search by category only
            searchResults = searchByCategorySlug(categorySlug);
            result.setSearchResults(searchResults);
        } else if (query != null) {
            // Search by query only
            searchResults = searchByQuery(query);
            result.setSearchResults(searchResults);
        } else {
            result.setSearchResults(searchResults);
        }

        result.setCategorySlug(categorySlug);
        result.setQuery(query);
        result.setResultSize(searchResults.size());

        return BaseResponse.ok(result);
    }

    @Override
    public List<Product> searchByCategorySlug(String categorySlug) {
        Category category = categoryRepository.findByCategorySlug(categorySlug);
        if (category != null) {
            return productRepository.findByCategory(category);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Product> searchByQuery(String query) {
        return productRepository.findByProductNameContaining(query);
    }

    @Override
    public List<Product> searchByCategorySlugAndQuery(String categorySlug, String query) {
        List<Product> byCategorySlug = searchByCategorySlug(categorySlug);
        List<Product> byQuery = searchByQuery(query);

        if (byCategorySlug.size() > 0) {
            Set<Product> combinedResults = new HashSet<>(byCategorySlug);
            combinedResults.addAll(byQuery);
            return new ArrayList<>(combinedResults);
        }

        return byQuery;
    }
}
