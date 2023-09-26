package com.radwija.jumpstartbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Setter @Getter @NoArgsConstructor
public class Product {
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
    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonBackReference
    private List<Item> item;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @JsonBackReference
    private List<ProductSnapshot> productSnapshots;
}
