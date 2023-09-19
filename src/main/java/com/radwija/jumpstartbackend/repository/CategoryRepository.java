package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(Long categoryId);
    List<Category> findAllByOrderByCategoryNameAsc();
    List<Category> findAllByOrderByCategoryNameDesc();
    Category findByCategorySlug(String categorySlug);
}
