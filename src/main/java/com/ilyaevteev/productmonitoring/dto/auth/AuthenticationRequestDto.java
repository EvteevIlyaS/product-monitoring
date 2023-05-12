package com.ilyaevteev.productmonitoring.dto.auth;

import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String username;

    private String password;
}
