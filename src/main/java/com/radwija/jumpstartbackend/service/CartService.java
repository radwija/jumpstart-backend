package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.Item;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    BigDecimal checkTotal(List<Item> items);
    BaseResponse<?> addProductToCart(String currentUserEmail, Long productId);
    BaseResponse<?> getMyCart(String email);
}
