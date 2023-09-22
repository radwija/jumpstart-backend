package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.exception.OutOfCartMaxTotalException;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.CartItemRepository;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public static BigDecimal maxAmount = new BigDecimal("9999999.99");

    @Override
    public BigDecimal checkTotal(List<CartItem> items) {

        BigDecimal total = BigDecimal.valueOf(0);
        for (CartItem item : items) {
            total.add(item.getItemTotal());
        }

        if (total.compareTo(maxAmount) > 0) {
            throw new OutOfCartMaxTotalException("Your cart total is greater than $9,999,999.99");
        }

        return total;
    }

    @Override
    public BaseResponse<?> addProductToCart(String currentUserEmail, Long productId) {
        CartItem cartItem = new CartItem();
        Cart cart = new Cart();
        return null;
    }
}
