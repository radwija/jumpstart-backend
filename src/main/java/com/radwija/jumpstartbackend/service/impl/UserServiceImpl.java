package com.radwija.jumpstartbackend.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import com.radwija.jumpstartbackend.exception.CredentialAlreadyTakenException;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.UserProfileRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public BaseResponse<?> saveUser(UserRegisterRequest request) {
        BaseResponse<User> response = new BaseResponse<>();
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new CredentialAlreadyTakenException("Email already taken!");
            }
            if (request.getPassword().equals("")) {
                throw new RuntimeException("Password is required!");
            }

            User newUser = new User();
            UserProfile userProfile = new UserProfile();

            userProfile.setUser(newUser);

            BeanUtils.copyProperties(request, newUser);
            BeanUtils.copyProperties(request, userProfile);

            newUser.setRole(ERole.ROLE_USER.toString());

            UUID uuid = UUID.randomUUID();
            newUser.setUuid(uuid.toString());

            String encodedPassword = passwordEncoder.encode(request.getPassword());
            newUser.setPassword(encodedPassword);
            newUser.setRegisteredAt(new Date());

            userRepository.save(newUser);
            userProfileRepository.save(userProfile);

            return BaseResponse.ok(newUser);
        } catch (RuntimeException e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
