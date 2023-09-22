package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
