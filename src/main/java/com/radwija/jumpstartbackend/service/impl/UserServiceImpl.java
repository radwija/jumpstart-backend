package com.radwija.jumpstartbackend.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import com.radwija.jumpstartbackend.exception.CredentialAlreadyTakenException;
import com.radwija.jumpstartbackend.exception.UserNotFoundException;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.repository.UserProfileRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import com.radwija.jumpstartbackend.service.EmailSenderService;
import com.radwija.jumpstartbackend.service.UserService;
import com.radwija.jumpstartbackend.utils.ServiceUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceUtils implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSenderService emailSenderService;

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
            Cart cart = new Cart();

            BeanUtils.copyProperties(request, newUser);
            BeanUtils.copyProperties(request, userProfile);

            newUser.setIsActive(false);
            newUser.setRole(ERole.ROLE_USER);

            UUID uuid = UUID.randomUUID();
            newUser.setUuid(uuid.toString());

            String encodedPassword = passwordEncoder.encode(request.getPassword());
            newUser.setPassword(encodedPassword);
            newUser.setRegisteredAt(new Date());

            userProfile.setUser(newUser);
            cart.setUser(newUser);
            newUser.setUserProfile(userProfile);
            newUser.setCart(cart);

            userRepository.save(newUser);

            response.setCode(200);
            response.setMessage("Registration done successfully! Check your email to activate your account.");
            response.setResult(newUser);

            emailSenderService.sendMail(newUser.getEmail(),
                    "Account Activation",
                    "Thanks for registering in Jumpstart E-commerce. Here is you activation URL to get started your journey in Jumpstart E-commerce!" +
                            "\n" +
                            "http://localhost:3000/account-activation/" + newUser.getUuid()
            );
            return response;
        } catch (RuntimeException e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public User getCurrentUser() {
        return super.getCurrentUser();
    }

    @Override
    public Boolean isActive(String email) {
        return userRepository.existsByEmailAndIsActive(email, true);
    }

    @Override
    public BaseResponse<?> activateUser(String uuid) {
        BaseResponse<User> response = new BaseResponse<>();
        try {
            User activatedUser = userRepository.findByUuid(uuid);
            if (activatedUser == null) {
                throw new UserNotFoundException("Account not found:(");
            }

            activatedUser.setIsActive(true);
            userRepository.save(activatedUser);

            response.setCode(200);
            response.setMessage("Account successfully activated!");
            response.setResult(activatedUser);

            return response;
        } catch (UserNotFoundException e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
            return response;
        }
    }
}
