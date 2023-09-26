package com.radwija.jumpstartbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Setter @Getter @NoArgsConstructor
public class ProductSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String slug;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private BigDecimal price;
    private Double weight;
    private Date productCreatedAt;
    private Date lastUpdatedAt;
    private Date snapshotAt;
    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "category_category_id")
    private Category category;

    private int quantity;
    private BigDecimal itemPriceTotal;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
//    @JsonBackReference
    private Product product;
}
