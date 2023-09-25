package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Order;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.entity.ProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSnapshotRepository extends JpaRepository<ProductSnapshot, Long> {
    List<ProductSnapshot> findAllByOrder(Order order);
    List<ProductSnapshot> findAllByProduct(Product product);
}
