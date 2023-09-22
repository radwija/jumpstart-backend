package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.CartItem;
import com.radwija.jumpstartbackend.entity.Product;
import com.radwija.jumpstartbackend.payload.request.CartItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CartItemService {
    BigDecimal checkItemTotal(CartItemRequest cartItemRequest);
    BaseResponse<?> saveCartItem(String email, CartItemRequest cartItemRequest);
}
