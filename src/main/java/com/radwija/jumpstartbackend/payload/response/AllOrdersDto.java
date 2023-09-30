package com.radwija.jumpstartbackend.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter @NoArgsConstructor
public class AllOrdersDto {
    private String filter;
    private String orderBy;
    private int orderNumbers;
    private List<CustomOrderDto> orders;
}
