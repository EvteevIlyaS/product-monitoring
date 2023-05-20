package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface UserService {
    User findByUsername(String username);

    Map<String, String> register(User user, BCryptPasswordEncoder passwordEncoder, String role, BindingResult bindingResult);

    Map<String, String> login(String username, String password, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider);

    void changeUserEmail(Authentication authentication, AuthenticationManager authenticationManager, String password, String newEmail,
                         BindingResult bindingResult);

    void changeUserPassword(Authentication authentication, AuthenticationManager authenticationManager, String oldPassword,
                            String newPassword, BCryptPasswordEncoder passwordEncoder, BindingResult bindingResult);
}
