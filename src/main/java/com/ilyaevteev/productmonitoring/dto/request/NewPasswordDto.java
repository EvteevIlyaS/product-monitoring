package com.ilyaevteev.productmonitoring.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPasswordDto {
    private String oldPassword;

    @Size(min = 5)
    private String newPassword;
}
