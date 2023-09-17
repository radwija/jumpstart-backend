package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.EGender;
import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import com.radwija.jumpstartbackend.exception.RefusedActionException;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.UserProfileRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public EGender getEnumGender(String gender) {
        switch (gender) {
            case "MALE":
                return EGender.MALE;
            case "FEMALE":
                return EGender.FEMALE;
        }
        return null;
    }

    @Override
    public BaseResponse<?> updateProfile(String currentUserEmail, UpdateUserRequest updateUserRequest) {
        BaseResponse<UserProfile> response = new BaseResponse<>();
        try {
            if (!isCurrentUserOrAdmin(currentUserEmail)) {
                throw new RefusedActionException("Access denied!");
            }


            response.setCode(200);
            response.setMessage("success");

            return response;
        } catch (Exception e) {
            System.out.println(e);
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public boolean isCurrentUserOrAdmin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("current user not found"));
        return user.getEmail().equals(email) || user.getRole() == ERole.ROLE_ADMIN;
    }
}
