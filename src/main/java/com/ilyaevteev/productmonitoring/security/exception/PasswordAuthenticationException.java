package com.ilyaevteev.productmonitoring.security.exception;

import org.springframework.security.core.AuthenticationException;

public class PasswordAuthenticationException extends AuthenticationException {
    public PasswordAuthenticationException(String msg) {
        super(msg);
    }
}
