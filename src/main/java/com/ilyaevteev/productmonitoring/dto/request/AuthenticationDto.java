package com.ilyaevteev.productmonitoring.dto.request;

import lombok.Data;

@Data
public class AuthenticationDto {
    private String username;

    private String password;
}
