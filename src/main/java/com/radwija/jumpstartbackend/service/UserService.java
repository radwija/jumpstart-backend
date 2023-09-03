package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;

public interface UserService {
    void saveUser(UserRegisterRequest userRegisterRequest);
}
