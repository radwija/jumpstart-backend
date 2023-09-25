package com.radwija.jumpstartbackend.repository;

import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import com.radwija.jumpstartbackend.entity.Order;
import com.radwija.jumpstartbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser(User user);
    List<Order> findAllByStatus(EOrderStatus status);
    List<Order> findAllByUserAndStatus(User user, EOrderStatus status);
}
