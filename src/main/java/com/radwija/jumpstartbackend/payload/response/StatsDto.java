package com.radwija.jumpstartbackend.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter @Getter @NoArgsConstructor
public class StatsDto {
    private BigDecimal revenue;
    private int customerNumbers;
    private int orderNumbers;
    private int productNumbers;
    private int categoryNumbers;
}
