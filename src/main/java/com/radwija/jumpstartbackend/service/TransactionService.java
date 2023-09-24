package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;

public interface TransactionService {
    BaseResponse<?> createPayment(BigDecimal fee);
    BaseResponse<?> completePayment(String token);
}
