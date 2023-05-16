package com.ilyaevteev.productmonitoring.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationDto {
    @Size(min = 5)
    private String username;

    @Email
    private String email;

    @Size(min = 5)
    private String password;
}
