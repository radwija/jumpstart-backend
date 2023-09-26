package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

import java.math.BigDecimal;

public interface StatisticService {
    BaseResponse<?> getAllStats(User user);
    BigDecimal getRevenue();
    int getCustomerNumbers();
    int getOrderNumbers();
    int getProductNumbers();
    int getCategoryNumbers();
}
