package com.radwija.jumpstartbackend.payload.response;

import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import com.radwija.jumpstartbackend.entity.ProductSnapshot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter @Getter @NoArgsConstructor
public class CustomOrderDto {
    private Long orderId;
    private Long userId;
    private String email;
    private String fullName;
    private EOrderStatus status;
    private Date createdAt;
    private Date updatedAt;
    private BigDecimal total;
    private List<ProductSnapshot> productSnapshots;
}
