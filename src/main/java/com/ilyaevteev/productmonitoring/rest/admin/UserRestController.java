package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.RegistrationRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.RegistrationResponseDto;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.service.UserService;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "userRestControllerAdmin")
@RequestMapping(value = "/api/v1/admin")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityDtoMapper entityDtoMapper;
    private final ObjectMapper objectMapper;

    @PostMapping
    @Operation(summary = "Выполнить процедуру регистрации администратора")
    public ResponseEntity<RegistrationResponseDto> addAdmin(@RequestBody @Valid RegistrationRequestDto requestDto, BindingResult bindingResult) {
        RegistrationResponseDto user = objectMapper.convertValue(userService.register(entityDtoMapper.toEntity(requestDto, User.class),
                passwordEncoder, "ROLE_ADMIN", bindingResult), RegistrationResponseDto.class);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}

