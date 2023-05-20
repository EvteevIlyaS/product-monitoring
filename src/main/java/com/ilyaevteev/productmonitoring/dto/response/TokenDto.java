package com.ilyaevteev.productmonitoring.dto.response;

import lombok.Data;

@Data
public class TokenDto {
    private String username;

    private String token;
}
