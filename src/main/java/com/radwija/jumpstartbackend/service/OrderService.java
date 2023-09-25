package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.entity.Order;
import com.radwija.jumpstartbackend.entity.ProductSnapshot;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.util.List;

public interface OrderService {
    BaseResponse<?> saveNewOrder(User user);
    void convertCartItemsToSnapshots(Order order, List<Item> items);
    BaseResponse<?> getAllOrders(User user, String status);
    BaseResponse<?> getMyOrders(User user, String status);
}
