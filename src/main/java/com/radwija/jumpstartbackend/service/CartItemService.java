package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.payload.request.ItemRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;

public interface CartItemService {
    BigDecimal checkItemTotal(ItemRequest itemRequest);
    BaseResponse<?> saveCartItem(String email, ItemRequest itemRequest);
    BaseResponse<?> deleteCartItemById(String email, Long cartItemId);
}
