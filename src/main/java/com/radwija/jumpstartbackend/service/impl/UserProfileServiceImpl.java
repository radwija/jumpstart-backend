package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.constraint.EGender;
import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import com.radwija.jumpstartbackend.exception.ProfileNotFoundException;
import com.radwija.jumpstartbackend.payload.request.UpdateUserRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.UserProfileRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.UserProfileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    public BaseResponse<?> updateProfile(User user, UpdateUserRequest updateUserRequest) {
        try {
            UserProfile profile = user.getUserProfile();
            if (profile == null) {
                throw new ProfileNotFoundException("Profile not found.");
            }

            BeanUtils.copyProperties(updateUserRequest, profile);
            String gender = updateUserRequest.getGender();
            if (gender.equalsIgnoreCase("MALE")) {
                profile.setGender(EGender.MALE);
            } else {
                profile.setGender(EGender.FEMALE);
            }

            userProfileRepository.save(profile);

            return BaseResponse.ok("Profile updated successfully!",profile);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return BaseResponse.badRequest(e.getMessage());
        }
    }

    @Override
    public boolean isCurrentUserOrAdmin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("current user not found"));
        return user.getEmail().equals(email) || user.getRole() == ERole.ROLE_ADMIN;
    }
}
