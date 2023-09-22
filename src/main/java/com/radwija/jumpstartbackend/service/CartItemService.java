package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.payload.request.CartItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;

public interface CartItemService {
    BigDecimal checkItemTotal(CartItemRequest cartItemRequest);
    BaseResponse<?> saveCartItem(String email, CartItemRequest cartItemRequest);
    BaseResponse<?> deleteCartItemById(String email, Long cartItemId);
}
