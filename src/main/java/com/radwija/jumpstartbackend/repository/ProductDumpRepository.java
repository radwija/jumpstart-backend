package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.ProductDump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDumpRepository extends JpaRepository<ProductDump, Long> {
}
