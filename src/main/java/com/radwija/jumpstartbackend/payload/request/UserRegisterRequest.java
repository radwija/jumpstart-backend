package com.radwija.jumpstartbackend.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserRegisterRequest {
    private String firstName;
    private String lastName;
    private String gender;
    private String username;
    private String email;
    private String password;
}
