package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface UserService {
    User findByUsername(String username);

    User register(User user, BCryptPasswordEncoder passwordEncoder, String role);

    String login(String username, String password, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider);

    void changeUserEmail(Authentication authentication, AuthenticationManager authenticationManager, String password, String newEmail);

    void changeUserPassword(Authentication authentication, AuthenticationManager authenticationManager,
                            String oldPassword, String newPassword, BCryptPasswordEncoder passwordEncoder);
}
