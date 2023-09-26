package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.constraint.EGender;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;

public interface UserProfileService {
    EGender getEnumGender(String gender);
    BaseResponse<?> updateProfile(User user, UpdateUserRequest updateUserRequest);

    boolean isCurrentUserOrAdmin(String email);
}
