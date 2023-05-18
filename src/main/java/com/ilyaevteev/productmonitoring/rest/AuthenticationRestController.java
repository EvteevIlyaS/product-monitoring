package com.ilyaevteev.productmonitoring.rest;

import com.ilyaevteev.productmonitoring.dto.auth.AuthenticationRequestDto;
import com.ilyaevteev.productmonitoring.dto.auth.RegistrationDto;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import com.ilyaevteev.productmonitoring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth/")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final UserService userService;

    private final EntityDtoMapper entityDtoMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("register")
    @Operation(summary = "Выполнить процедуру регистрации пользователя")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegistrationDto requestDto, BindingResult bindingResult) {
        User user = userService.register(entityDtoMapper.toEntity(requestDto, User.class), passwordEncoder, "ROLE_USER", bindingResult);

        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }

    @GetMapping("login")
    @Operation(summary = "Выполнить процедуру авторизации пользователя")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationRequestDto requestDto) {
        Map<String, String> response = new HashMap<>();
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        String token = userService.login(username, password, authenticationManager, jwtTokenProvider);
        response.put("username", username);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
