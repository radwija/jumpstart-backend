package com.radwija.jumpstartbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.radwija.jumpstartbackend.constraint.ERole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Setter
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    @Column(unique = true)
    private String uuid;

    @JsonIgnore
    private String password;

    private Date registeredAt;

    @Enumerated(EnumType.STRING)
    private ERole role;

    private Boolean isActive;
}
