package com.radwija.jumpstartbackend.payload.response;

import com.radwija.jumpstartbackend.entity.Order;
import com.radwija.jumpstartbackend.entity.ProductSnapshot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter @Getter @NoArgsConstructor
public class OrderDto {
    private String filter;
    private String orderBy;
    private int orderNumbers;
    private List<Order> orders;
}
