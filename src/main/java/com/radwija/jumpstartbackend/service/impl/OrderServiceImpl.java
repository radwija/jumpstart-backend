package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import com.radwija.jumpstartbackend.entity.Order;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.payload.response.OrderDto;
import com.radwija.jumpstartbackend.repository.OrderRepository;
import com.radwija.jumpstartbackend.service.OrderService;
import com.radwija.jumpstartbackend.utils.OrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends OrderUtils implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public BaseResponse<?> getAllOrders(User user, String status) {
        BaseResponse<Order> response = new BaseResponse<>();
        try {
            String email = user.getEmail();
            if (!isAdmin(email)) {
                return BaseResponse.forbidden();
            }

            List<Order> orders = orderRepository.findAll();

            OrderDto result = new OrderDto();
            switch (status) {
                case "pending":
                    orders = orderRepository.findAllByStatus(EOrderStatus.PENDING);
                    break;
                case "completed":
                    orders = orderRepository.findAllByStatus(EOrderStatus.COMPLETED);
                    break;
                case "cancelled":
                    orders = orderRepository.findAllByStatus(EOrderStatus.CANCELLED);
                    break;
                default:
                    orders = orderRepository.findAll();
                    break;
            }

            result.setFilter(status);
            result.setOrderNumbers(orders.size());
            result.setOrders(orders);


            return BaseResponse.ok(orders);
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> getMyOrders(User user, String status) {
        try {
            List<Order> myOrders;
            OrderDto result = new OrderDto();
            switch (status) {
                case "pending":
                    myOrders = orderRepository.findAllByUserAndStatus(user, EOrderStatus.PENDING);
                    break;
                case "completed":
                    myOrders = orderRepository.findAllByUserAndStatus(user, EOrderStatus.COMPLETED);
                    break;
                case "cancelled":
                    myOrders = orderRepository.findAllByUserAndStatus(user, EOrderStatus.CANCELLED);
                    break;
                default:
                    myOrders = orderRepository.findAllByUser(user);
                    break;
            }

            result.setFilter(status);
            result.setOrderNumbers(myOrders.size());
            result.setOrders(myOrders);

            return BaseResponse.ok(result);
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }
}
