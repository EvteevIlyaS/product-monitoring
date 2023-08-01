package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.auth.Role;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.repository.auth.RoleRepository;
import com.ilyaevteev.productmonitoring.repository.auth.UserRepository;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.PasswordAuthenticationException;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import com.ilyaevteev.productmonitoring.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByUsername_checkReturnedValue() {
        String username = "user";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User userRes = userService.findByUsername(username);

        verify(userRepository, times(1)).findByUsername(username);
        assertThat(userRes).isEqualTo(user);
    }

    @Test
    void findByUsername_checkThrowException() {
        String username = "user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername(username))
                .hasMessage("No users found");
    }

    @Test
    void register_checkReturnedValue() {
        User user = new User();
        String username = "user";
        user.setUsername(username);
        Map<String, String> userMap = Map.of("username", username);
        String password = "123";
        user.setPassword(password);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String roleName = "ROLE_USER";
        Role role = new Role();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);

        Map<String, String> userRes = userService.register(user, passwordEncoder, roleName, bindingResult);

        assertThat(userRes).isEqualTo(userMap);
    }

    @Test
    void register_checkFirstThrowException() {
        User user = new User();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String roleName = "ROLE_USER";
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(user, passwordEncoder, roleName, bindingResult))
                .hasMessage("Role not found");
    }

    @Test
    void register_checkSecondThrowException() {
        User user = new User();
        String password = "123";
        user.setPassword(password);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String roleName = "ROLE_USER";
        Role role = new Role();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> userService.register(user, passwordEncoder, roleName, bindingResult))
                .hasMessage("Wrong registration data");
    }

    @Test
    void register_checkThirdThrowException() {
        User user = new User();
        String password = "123";
        user.setPassword(password);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String roleName = "ROLE_USER";
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThatThrownBy(() -> userService.register(user, passwordEncoder, roleName, bindingResult))
                .hasMessage("Data structure violation");
    }

    @Test
    void login_checkMethodInvocation() {
        String username = "user";
        String password = "123";
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        User user = new User();
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createToken(anyString(), any())).thenReturn("token");

        userService.login(username, password, authenticationManager, jwtTokenProvider);

        verify(jwtTokenProvider, times(1)).createToken(username, user.getRoles());
    }

    @Test
    void login_checkFirstThrowException() {
        String username = "user";
        String password = "123";
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> userService.login(username, password, authenticationManager, jwtTokenProvider))
                .hasMessage("Wrong authentication data");
    }

    @Test
    void login_checkSecondThrowException() {
        String username = "user";
        String password = "123";
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(username, password, authenticationManager, jwtTokenProvider))
                .hasMessage("No users found");
    }

    @Test
    void changeUserEmail_checkMethodInvocation() {
        String oldPassword = "321";
        String email = "user@gmail.com";
        String username = "name";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(authentication.getName()).thenReturn(username);

        userService.changeUserEmail(authentication, authenticationManager, oldPassword, email, bindingResult);

        verify(userRepository, times(1)).updateUserEmail(authentication.getName(), email);
    }

    @Test
    void changeUserEmail_checkFirstThrowException() {
        String oldPassword = "321";
        String email = "user@gmail.com";
        String username = "name";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn(username);
        doThrow(new PasswordAuthenticationException("Wrong password")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> userService.changeUserEmail(authentication, authenticationManager, oldPassword, email, bindingResult))
                .hasMessage("Wrong password input");
    }

    @Test
    void changeUserEmail_checkSecondThrowException() {
        String oldPassword = "321";
        String email = "user@gmail.com";
        String username = "name";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(authentication.getName()).thenReturn(username);
        doThrow(new RuntimeException()).when(userRepository).updateUserEmail(username, email);

        assertThatThrownBy(() -> userService.changeUserEmail(authentication, authenticationManager, oldPassword, email, bindingResult))
                .hasMessage("Wrong email input");
    }

    @Test
    void changeUserEmail_checkThirdThrowException() {
        String oldPassword = "321";
        String email = "user@gmail.com";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserEmail(authentication, authenticationManager, oldPassword, email, bindingResult))
                .hasMessage("Data structure violation");
    }

    @Test
    void changeUserPassword_checkMethodInvocation() {
        String oldPassword = "321";
        String newPassword = "123";
        String username = "name";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(authentication.getName()).thenReturn(username);

        userService.changeUserPassword(authentication, authenticationManager, oldPassword, newPassword, passwordEncoder, bindingResult);

        verify(userRepository, times(1)).updateUserPassword(any(), any());
    }

    @Test
    void changeUserPassword_checkFirstThrowException() {
        String oldPassword = "321";
        String newPassword = "123";
        String username = "name";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authentication.getName()).thenReturn(username);
        doThrow(new PasswordAuthenticationException("Wrong password")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> userService.changeUserPassword(authentication, authenticationManager, oldPassword,
                newPassword, passwordEncoder, bindingResult))
                .hasMessage("Wrong old password input");
    }

    @Test
    void changeUserPassword_checkSecondThrowException() {
        String oldPassword = "321";
        String newPassword = "123";
        String username = "name";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(authentication.getName()).thenReturn(username);
        doThrow(new RuntimeException()).when(userRepository).updateUserPassword(username , oldPassword);

        assertThatThrownBy(() -> userService.changeUserPassword(authentication, authenticationManager, oldPassword,
                newPassword, passwordEncoder, bindingResult))
                .hasMessage("Wrong new password input");
    }

    @Test
    void changeUserPassword_checkThirdThrowException() {
        String oldPassword = "321";
        String newPassword = "123";
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        Authentication authentication = mock(Authentication.class);
        BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThatThrownBy(() -> userService.changeUserPassword(authentication, authenticationManager, oldPassword,
                newPassword, passwordEncoder, bindingResult))
                .hasMessage("Data structure violation");
    }
}