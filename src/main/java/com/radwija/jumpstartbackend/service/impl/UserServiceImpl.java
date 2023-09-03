package com.radwija.jumpstartbackend.service.impl;

import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.repository.UserProfileRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public void saveUser(UserRegisterRequest userRegisterRequest) {
        User newUser = new User();
        UserProfile userProfile = new UserProfile();

        userProfile.setUser(newUser);

        BeanUtils.copyProperties(userRegisterRequest, newUser);
        BeanUtils.copyProperties(userRegisterRequest, userProfile);

        userRepository.save(newUser);
        userProfileRepository.save(userProfile);
    }
}
