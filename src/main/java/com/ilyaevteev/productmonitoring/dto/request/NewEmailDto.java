package com.ilyaevteev.productmonitoring.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class NewEmailDto {
    private String password;

    @Email
    private String newEmail;
}
