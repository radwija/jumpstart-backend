package com.radwija.jumpstartbackend.controller;

import com.radwija.jumpstartbackend.payload.request.*;
import com.radwija.jumpstartbackend.payload.response.AuthResponse;
import com.radwija.jumpstartbackend.payload.response.BaseResponse;
import com.radwija.jumpstartbackend.security.jwt.JwtUtil;
import com.radwija.jumpstartbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        final BaseResponse<?> response = userService.saveUser(request);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody LoginRequest loginRequest) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails user = (UserDetails) authentication.getPrincipal();
            Set<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            boolean isActive = userService.isActive(loginRequest.getEmail());
            if (!isActive) {
                System.out.println("Your account is not active yet");
                return ResponseEntity.status(401)
                        .body("Your account is not active yet, please activate your account");
            }
            String jwtToken = jwtUtil.generateToken(user.getUsername());
            AuthResponse authResponse = new AuthResponse();
            authResponse.setEmail(user.getUsername());
            authResponse.setAccessToken(jwtToken);
            authResponse.setRole(roles);

            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            System.out.println(e);
            return ResponseEntity.status(401)
                    .body("Authentication failed. Invalid email or password.");
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(500)
                    .body("Internal server error");
        }
    }

    @GetMapping("/account-activation/{uuid}")
    public ResponseEntity<?> activateUser(@PathVariable("uuid") String uuid) {
        final BaseResponse<?> response = userService.activateUser(uuid);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/acc")
    public ResponseEntity<?> findAccountByUuid(@RequestParam("find") String uuid) {
        final BaseResponse<?> response = userService.findAccountByUuid(uuid);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/find-account")
    public ResponseEntity<?> findAccountByEmail(@RequestBody FindAccountRequest findAccountRequest) {
        final BaseResponse<?> response = userService.updateUuidResetPassword(findAccountRequest.getEmail());
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        final BaseResponse<?> response = userService.updatePassword(updatePasswordRequest);
        if (response.getCode() == 200) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

}
