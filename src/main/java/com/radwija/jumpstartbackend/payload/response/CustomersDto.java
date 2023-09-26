package com.radwija.jumpstartbackend.payload.response;

import com.radwija.jumpstartbackend.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter @NoArgsConstructor
public class CustomersDto {
    private int customerNumbers;
    private List<CustomerDto> customers;
}
