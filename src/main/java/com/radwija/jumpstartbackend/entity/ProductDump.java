package com.radwija.jumpstartbackend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Setter @Getter @NoArgsConstructor
public class ProductDump {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String slug;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private BigDecimal price;
    private Long stock;
    private Double weight;
    private Date createdAt;
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


}