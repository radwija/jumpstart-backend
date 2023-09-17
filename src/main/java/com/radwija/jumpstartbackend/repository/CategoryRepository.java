package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
