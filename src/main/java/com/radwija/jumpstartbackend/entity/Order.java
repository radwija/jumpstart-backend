package com.radwija.jumpstartbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.radwija.jumpstartbackend.constraint.EOrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Date createdAt;
    private Date updatedAt;
    private BigDecimal total;

//    @JsonBackReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<ProductSnapshot> productSnapshots;

    @Enumerated(EnumType.STRING)
    private EOrderStatus status;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
