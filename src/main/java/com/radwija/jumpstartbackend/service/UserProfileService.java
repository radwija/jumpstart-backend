package com.radwija.jumpstartbackend.service;

import com.radwija.jumpstartbackend.constraint.EGender;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    EGender getEnumGender(String gender);
    BaseResponse<?> updateProfile(User user, UpdateUserRequest updateUserRequest);
    BaseResponse<?> updateProfilePicture(User user, MultipartFile profilePicture);

    boolean isCurrentUserOrAdmin(String email);
}
