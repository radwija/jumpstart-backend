package com.radwija.jumpstartbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Setter @Getter @NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private int quantity;
    @Transient
    private BigDecimal itemPriceTotal;

    private Date createdAt;
    private Date updatedAt;

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
