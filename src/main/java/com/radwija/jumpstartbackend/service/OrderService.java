package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

public interface OrderService {
    BaseResponse<?> getAllOrders(User user, String status);
    BaseResponse<?> getMyOrders(User user, String status);
}
