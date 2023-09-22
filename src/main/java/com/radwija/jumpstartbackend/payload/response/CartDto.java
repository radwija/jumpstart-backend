package com.radwija.jumpstartbackend.payload.response;

import com.radwija.jumpstartbackend.entity.CartItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CartDto {
    private Long cartId;
    private Long userId;
    @Transient
    private int itemNumbers;
    private BigDecimal total;
    private List<CartItem> cartItems;
}
