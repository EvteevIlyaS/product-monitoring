package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.auth.Role;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.repository.auth.RoleRepository;
import com.ilyaevteev.productmonitoring.repository.auth.UserRepository;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import com.ilyaevteev.productmonitoring.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            String message = "No users found by name: " + username;
            log.error(message);
            throw new RuntimeException(message);
        }

        return user.get();
    }

    @Override
    @Transactional
    public User register(User user, BCryptPasswordEncoder passwordEncoder, String role) {
        User registeredUser;

        Optional<Role> roleOptional = roleRepository.findByName(role);

        if (roleOptional.isEmpty()) {
            String message = "Role not found";
            log.error(message);
            throw new RuntimeException(message);
        }

        Role roleUser = roleOptional.get();

        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);

        try {
            registeredUser = userRepository.save(user);
        } catch (Exception e) {
            String message = "Wrong registration data";
            log.error(message);
            throw new RuntimeException(message);
        }

        return registeredUser;
    }

    @Override
    @Transactional
    public String login(String username, String password, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            String message = "Wrong authentication data";
            log.error(message);
            throw new RuntimeException(message);
        }

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            String message = "No users found by name: " + username;
            log.error(message);
            throw new RuntimeException(message);
        }

        return jwtTokenProvider.createToken(username, user.get().getRoles());
    }

    @Override
    @Transactional
    public void changeUserEmail(Authentication authentication, AuthenticationManager authenticationManager,
                                String password, String newEmail) {
        String username = authentication.getName();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            userRepository.updateUserEmail(username, newEmail);
        } catch (AuthenticationException e) {
            String message = "Wrong password input";
            log.error(message);
            throw new RuntimeException(message);
        } catch (Exception e) {
            String message = "Wrong email input";
            log.error(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    @Transactional
    public void changeUserPassword(Authentication authentication, AuthenticationManager authenticationManager,
                                   String oldPassword, String newPassword, BCryptPasswordEncoder passwordEncoder) {
        String username = authentication.getName();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
            userRepository.updateUserPassword(username, passwordEncoder.encode(newPassword));
        } catch (AuthenticationException e) {
            String message = "Wrong old password input";
            log.error(message);
            throw new RuntimeException(message);
        } catch (Exception e) {
            String message = "Wrong new password input";
            log.error(message);
            throw new RuntimeException(message);
        }
    }
}
