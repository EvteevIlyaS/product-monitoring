package com.ilyaevteev.productmonitoring.dto.auth;

import lombok.Data;

@Data
public class NewEmailDto {
    private String password;

    private String newEmail;
}
