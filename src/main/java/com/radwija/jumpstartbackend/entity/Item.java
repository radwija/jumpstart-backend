package com.radwija.jumpstartbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.radwija.jumpstartbackend.constraint.EItemStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Setter @Getter @NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private int quantity;
    @Transient
    private BigDecimal itemPriceTotal;

    private Date createdAt;
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    private EItemStatus status;

    @ManyToOne
    @JoinColumn(name = "product_id")
//    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    public BigDecimal getItemPriceTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
