package com.ilyaevteev.productmonitoring.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.AuthenticationDto;
import com.ilyaevteev.productmonitoring.dto.request.RegistrationRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.RegistrationResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.TokenDto;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.security.jwt.JwtTokenProvider;
import com.ilyaevteev.productmonitoring.service.UserService;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController(value = "userRestControllerAuth")
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    private final EntityDtoMapper entityDtoMapper;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @Operation(summary = "Выполнить процедуру регистрации пользователя")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody @Valid RegistrationRequestDto requestDto, BindingResult bindingResult) {
        RegistrationResponseDto user = objectMapper.convertValue(userService.register(entityDtoMapper.toEntity(requestDto, User.class),
                passwordEncoder, "ROLE_USER", bindingResult), RegistrationResponseDto.class);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Выполнить процедуру авторизации пользователя")
    public TokenDto login(@RequestBody AuthenticationDto authenticationDto) {
        return objectMapper.convertValue(userService.login(authenticationDto.getUsername(), authenticationDto.getPassword(),
                authenticationManager, jwtTokenProvider), TokenDto.class);
    }
}
