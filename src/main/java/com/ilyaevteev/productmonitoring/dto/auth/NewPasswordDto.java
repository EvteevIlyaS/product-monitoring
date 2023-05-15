package com.ilyaevteev.productmonitoring.dto.auth;

import lombok.Data;

@Data
public class NewPasswordDto {
    private String oldPassword;

    private String newPassword;
}
