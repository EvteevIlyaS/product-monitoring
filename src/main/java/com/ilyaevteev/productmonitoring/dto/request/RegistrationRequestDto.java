package com.ilyaevteev.productmonitoring.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationRequestDto {
    @Size(min = 5)
    private String username;

    @Email
    private String email;

    @Size(min = 5)
    private String password;
}
