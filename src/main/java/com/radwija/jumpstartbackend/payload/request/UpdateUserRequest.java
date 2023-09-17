package com.radwija.jumpstartbackend.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String city;
    private String country;
    private String address;
}
