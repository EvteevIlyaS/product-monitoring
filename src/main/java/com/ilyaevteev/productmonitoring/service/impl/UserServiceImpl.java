package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.FailedDependencyException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.NotFoundException;
import com.ilyaevteev.productmonitoring.model.auth.Role;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.repository.auth.RoleRepository;
import com.ilyaevteev.productmonitoring.repository.auth.UserRepository;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import com.ilyaevteev.productmonitoring.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            String message = "No users found";
            log.error(message);
            throw new NotFoundException(message);
        });
    }

    @Override
    @Transactional
    public Map<String, String> register(User user, BCryptPasswordEncoder passwordEncoder, String role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = "Data structure violation";
            log.error(message);
            throw new BadRequestException(message);
        }

        Role roleUser = roleRepository.findByName(role).orElseGet(() -> {
            String message = "Role not found";
            log.error(message);
            throw new BadRequestException(message);
        });

        List<Role> userRoles = List.of(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);

        try {
            User registeredUser = userRepository.save(user);
            return Map.of("username", registeredUser.getUsername());

        } catch (Exception e) {
            String message = "Wrong registration data";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    @Transactional
    public Map<String, String> login(String username, String password, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            String message = "Wrong authentication data";
            log.error(message);
            throw new BadRequestException(message);
        }

        User user = userRepository.findByUsername(username).orElseGet(() -> {
            String message = "No users found";
            log.error(message);
            throw new NotFoundException(message);
        });

        return Map.of("username", username, "token", jwtTokenProvider.createToken(username, user.getRoles()));
    }

    @Override
    @Transactional
    public void changeUserEmail(Authentication authentication, AuthenticationManager authenticationManager,
                                String password, String newEmail, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = "Data structure violation";
            log.error(message);
            throw new BadRequestException(message);
        }

        String username = authentication.getName();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            userRepository.updateUserEmail(username, newEmail);
        } catch (AuthenticationException e) {
            String message = "Wrong password input";
            log.error(message);
            throw new FailedDependencyException(message);
        } catch (Exception e) {
            String message = "Wrong email input";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    @Transactional
    public void changeUserPassword(Authentication authentication, AuthenticationManager authenticationManager, String oldPassword,
                                   String newPassword, BCryptPasswordEncoder passwordEncoder, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = "Data structure violation";
            log.error(message);
            throw new BadRequestException(message);
        }

        String username = authentication.getName();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
            userRepository.updateUserPassword(username, passwordEncoder.encode(newPassword));
        } catch (AuthenticationException e) {
            String message = "Wrong old password input";
            log.error(message);
            throw new FailedDependencyException(message);
        } catch (Exception e) {
            String message = "Wrong new password input";
            log.error(message);
            throw new BadRequestException(message);
        }
    }
}
