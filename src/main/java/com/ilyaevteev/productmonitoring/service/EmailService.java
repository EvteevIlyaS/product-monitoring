package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.auth.User;

public interface EmailService {
    void sendEmail(User user);
}
