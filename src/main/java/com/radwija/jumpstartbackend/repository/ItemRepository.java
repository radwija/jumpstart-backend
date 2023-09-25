package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.constraint.EItemStatus;
import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByProduct(Product product);
    Item findByProductAndStatus(Product product, EItemStatus status);
    List<Item> findByCartAndProductIsNotNullAndStatus(Cart cart, EItemStatus status);
    List<Item> findAllByProductAndStatus(Product product, EItemStatus status);
}
