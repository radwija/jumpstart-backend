package com.radwija.jumpstartbackend.utils;

import com.radwija.jumpstartbackend.constraint.ERole;
import com.radwija.jumpstartbackend.entity.User;
import com.radwija.jumpstartbackend.repository.CartRepository;
import com.radwija.jumpstartbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ServiceUtils {
    @Autowired
    private UserRepository userRepository;

    protected boolean isAdmin(String email) {
        return userRepository.existsByEmailAndRole(email, ERole.ROLE_ADMIN);
    }

    protected User getCurrentUser() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByEmail(currentUserEmail);
        return userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new UsernameNotFoundException("current user not found"));
    }
}
