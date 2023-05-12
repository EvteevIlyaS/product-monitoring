package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface UserService {
    User findByUsername(String username);

    User register(User user, BCryptPasswordEncoder passwordEncoder, String role);

    String login(String username, String password, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider);

    void changeUserEmail(String username, String email);

    void changeUserPassword(String username, String password, BCryptPasswordEncoder passwordEncoder);
}
