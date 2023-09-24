package com.radwija.jumpstartbackend.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class ItemRequest {
    private Long productId;
    private int quantity;
    private String requestFrom;
}
