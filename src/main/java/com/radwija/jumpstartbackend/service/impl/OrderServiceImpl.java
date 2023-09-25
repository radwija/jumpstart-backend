package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.EItemStatus;
import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import com.radwija.jumpstartbackend.entity.*;
import com.radwija.jumpstartbackend.exception.CartNotFoundException;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.payload.response.AllOrdersDto;
import com.radwija.jumpstartbackend.payload.response.CustomOrderDto;
import com.radwija.jumpstartbackend.payload.response.OrderDto;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.ItemRepository;
import com.radwija.jumpstartbackend.repository.OrderRepository;
import com.radwija.jumpstartbackend.repository.ProductSnapshotRepository;
import com.radwija.jumpstartbackend.service.OrderService;
import com.radwija.jumpstartbackend.utils.OrderUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends OrderUtils implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductSnapshotRepository productSnapshotRepository;

    @Override
    public BaseResponse<?> saveNewOrder(User user) {
        try {
            Cart cart = cartRepository.findByUser(user)
                    .orElseThrow(() -> new CartNotFoundException("Cart not found."));
            List<Item> items = itemRepository.findByCartAndProductIsNotNullAndStatus(cart, EItemStatus.IN_CART);
            BigDecimal total = new BigDecimal("0");

            for (com.radwija.jumpstartbackend.entity.Item item : items) {
                total = total.add(item.getItemPriceTotal());
            }

            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setTotal(total);
            newOrder.setStatus(EOrderStatus.PENDING);
            newOrder.setCreatedAt(new Date());
            orderRepository.save(newOrder);
            convertCartItemsToSnapshots(newOrder, items);
            orderRepository.save(newOrder);
            return BaseResponse.ok(newOrder);
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    @Override
    public void convertCartItemsToSnapshots(Order order, List<Item> items) {
        for (Item item : items) {
            ProductSnapshot snapshot = new ProductSnapshot();
            Product product = item.getProduct();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
            Date snapshotAt = new Date();
            String formattedDate = dateFormat.format(snapshotAt);

            snapshot.setOrder(order);
            snapshot.setQuantity(item.getQuantity());
            snapshot.setItemPriceTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            snapshot.setProduct(product);
            snapshot.setCategory(product.getCategory());
            snapshot.setProductName(product.getProductName());
            snapshot.setSlug("snapshot_" +
                    product.getSlug() +
                    "_" + item.getItemId() +
                    "_" + formattedDate
            );
            snapshot.setDescription(product.getDescription());
            snapshot.setPrice(product.getPrice());
            snapshot.setWeight(product.getWeight());
            snapshot.setProductCreatedAt(product.getCreatedAt());
            snapshot.setLastUpdatedAt(product.getUpdatedAt());
            snapshot.setSnapshotAt(snapshotAt);

            productSnapshotRepository.save(snapshot);
        }
    }

    @Override
    public BaseResponse<?> getAllOrders(User user, String status) {
        BaseResponse<Order> response = new BaseResponse<>();
        try {
            String email = user.getEmail();
            if (!isAdmin(email)) {
                return BaseResponse.forbidden();
            }

            List<Order> orders;

            AllOrdersDto result = new AllOrdersDto();
            List<CustomOrderDto> customOrderDtos = new ArrayList<>();
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
            for (Order order : orders) {
                User customer = order.getUser();
                String firstName = customer.getUserProfile().getFirstName();
                String lastName = customer.getUserProfile().getLastName();

                CustomOrderDto customOrderDto = new CustomOrderDto();
                BeanUtils.copyProperties(order, customOrderDto);
                customOrderDto.setUserId(customer.getUserId());
                customOrderDto.setEmail(customer.getEmail());
                customOrderDto.setFullName(firstName + " " + lastName);
                customOrderDtos.add(customOrderDto);
            }

            result.setFilter(status);
            result.setOrderNumbers(orders.size());
            result.setOrders(customOrderDtos);

            return BaseResponse.ok(result);
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
