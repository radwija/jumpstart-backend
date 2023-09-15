package com.radwija.jumpstartbackend.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter @Getter @NoArgsConstructor
public class AuthResponse {
    private String email;
    private String accessToken;
    private Set<String> Role;
}
