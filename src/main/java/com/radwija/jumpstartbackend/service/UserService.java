package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.UpdatePasswordRequest;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import org.springframework.security.core.context.SecurityContextHolder;

public interface UserService {
    BaseResponse<?> saveUser(UserRegisterRequest userRegisterRequest);
    User getCurrentUser();
    Boolean isActive(String email);
    BaseResponse<?> activateUser(String uuid);
    BaseResponse<?> findAccountByUuid(String uuid);
    BaseResponse<?> updateUuidResetPassword(String email);
    BaseResponse<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);
    BaseResponse<?> showAllUsers(User user);
}
