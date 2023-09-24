package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByProduct(Product product);
}
