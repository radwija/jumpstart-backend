package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

public interface UserService {
    BaseResponse<?> saveUser(UserRegisterRequest userRegisterRequest);
}
