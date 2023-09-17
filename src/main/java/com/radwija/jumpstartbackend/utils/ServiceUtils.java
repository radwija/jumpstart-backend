package com.radwija.jumpstartbackend.utils;

import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.exception.UsernameNotFoundException;
import com.radwija.jumpstartbackend.repository.UserRepository;

public class ServiceUtils {
    public static boolean isCurrentUserOrAdmin(UserRepository userRepository, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("current user not found"));
        return user.getEmail().equals(email) || user.getRole() == ERole.ROLE_ADMIN;
    }
}
