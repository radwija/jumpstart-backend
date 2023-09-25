package com.radwija.jumpstartbackend.payload.response;

import com.radwija.jumpstartbackend.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter @NoArgsConstructor
public class OrderDto {
    private String filter;
    private int orderNumbers;
    private List<Order> orders;
}
