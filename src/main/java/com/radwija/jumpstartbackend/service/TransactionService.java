package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;

public interface TransactionService {
    BaseResponse<?> createPayment(User user);
    BaseResponse<?> completePayment(User user, String token);
}
