package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByProduct(Product product);
}
