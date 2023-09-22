package com.radwija.jumpstartbackend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Setter @Getter @NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private int quantity;
    @Transient
    private BigDecimal itemTotal;

    @OneToOne
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public BigDecimal getItemTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    private void setItemTotal(BigDecimal itemTotal) {}
}
