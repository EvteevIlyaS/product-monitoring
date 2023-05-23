package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.request.NewEmailDto;
import com.ilyaevteev.productmonitoring.dto.request.NewPasswordDto;
import com.ilyaevteev.productmonitoring.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "userRestControllerUser")
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @PutMapping(value = "/email")
    @Operation(summary = "Изменить почту текущего пользователя")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid NewEmailDto newEmailDto,
                                         BindingResult bindingResult, Authentication authentication) {
        userService.changeUserEmail(authentication, authenticationManager, newEmailDto.getPassword(),
                newEmailDto.getNewEmail(), bindingResult);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<?> changePassword(@RequestBody @Valid NewPasswordDto newPasswordDto,
                                            BindingResult bindingResult, Authentication authentication) {
        userService.changeUserPassword(authentication, authenticationManager, newPasswordDto.getOldPassword(),
                newPasswordDto.getNewPassword(), passwordEncoder, bindingResult);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
