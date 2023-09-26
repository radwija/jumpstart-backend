package com.radwija.jumpstartbackend.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.Cart;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.entity.UserProfile;
import com.radwija.jumpstartbackend.exception.CredentialAlreadyTakenException;
import com.radwija.jumpstartbackend.exception.UserNotFoundException;
import com.radwija.jumpstartbackend.payload.request.UpdatePasswordRequest;
import com.radwija.jumpstartbackend.payload.request.UserRegisterRequest;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.payload.response.CustomerDto;
import com.radwija.jumpstartbackend.payload.response.CustomersDto;
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

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                throw new UserNotFoundException("Account not found.");
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

    @Override
    public BaseResponse<?> findAccountByUuid(String uuid) {
        try {
            User user = userRepository.findByUuid(uuid);
            if (user == null) {
                throw  new UserNotFoundException("User not found.");
            }
            return BaseResponse.ok(user);
        } catch (Exception e) {
            return BaseResponse.notFound(e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> updateUuidResetPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Account not found with email " + email));
            String fullName = user.getUserProfile().getFirstName() + " " + user.getUserProfile().getLastName();

            UUID updatedUuid = UUID.randomUUID();
            String parsedUuid = updatedUuid.toString();
            user.setUuid(parsedUuid);
            userRepository.save(user);

            String emailContent =
                    "Dear " + fullName + ",\n\n" +
                            "We recently received a request to reset your password for your Jumpstart E-commerce account. To proceed with resetting your password, please click the link below:\n\n" +
                            "http://localhost:3000/reset-password?reset=" + parsedUuid + "\n\n" +
                            "If you didn't initiate this request, you can safely ignore this email and your password will remain unchanged.\n\n" +
                            "Thank you for using Jumpstart E-commerce!\n\n" +
                            "Best Regards,\n" +
                            "The Jumpstart E-commerce Team";

            emailSenderService.sendMail(
                    email,
                    "Reset Password Link",
                    emailContent
            );
            return BaseResponse.ok("Reset password link has sent to your email.");
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }

    }

    @Override
    public BaseResponse<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        try {
            User user = userRepository.findByUuid(updatePasswordRequest.getUuid());
            if (user == null) {
                throw new UserNotFoundException("User not found.");
            }

            String encodedPassword = passwordEncoder.encode(updatePasswordRequest.getPassword());
            UUID updatedUuid = UUID.randomUUID();

            user.setPassword(encodedPassword);
            user.setUuid(updatedUuid.toString());
            userRepository.save(user);

            return BaseResponse.ok("Password updated successfully!");
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }

    }

    @Override
    public BaseResponse<?> showAllUsers(User user) {
        try {
            String email = user.getEmail();
            if (!isAdmin(email)) {
                return BaseResponse.forbidden();
            }
            CustomersDto result = new CustomersDto();
            List<User> users = userRepository.findAllByRole(ERole.ROLE_USER);
            List<CustomerDto> customerDtos = new ArrayList<>();

            for (User mappedUser : users) {
                UserProfile profile = mappedUser.getUserProfile();
                String firstName = profile.getFirstName();
                String lastName = profile.getLastName();
                CustomerDto customerDto = new CustomerDto();

                BeanUtils.copyProperties(mappedUser, customerDto);
                customerDto.setFullName(firstName + " " + lastName);
                customerDtos.add(customerDto);
            }

            result.setCustomerNumbers(users.size());
            result.setCustomers(customerDtos);

            return BaseResponse.ok(result);
        } catch (Exception e) {
            return BaseResponse.badRequest(e.getMessage());
        }
    }
}
