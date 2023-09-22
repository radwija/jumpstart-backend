package com.radwija.jumpstartbackend.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class CartItemRequest {
    private Long productId;
    private int quantity;
}
