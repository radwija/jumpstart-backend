package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.constraint.EGender;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface UserProfileService {
    EGender getEnumGender(String gender);
    BaseResponse<?> updateProfile(String currentUserEmail, UpdateUserRequest updateUserRequest);
    boolean isCurrentUserOrAdmin(String email);
}
