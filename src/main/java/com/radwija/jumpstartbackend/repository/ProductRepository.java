package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Category;
import com.radwija.jumpstartbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    boolean existsBySlug(String slug);
    Product findBySlug(String slug);
    List<Product> findByCategory(Category category);
    List<Product> findByProductNameContaining(String query);
}
