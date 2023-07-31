package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.sender.email}")
    private String senderEmail;
    @Value("${spring.mail.sender.text}")
    private String senderText;

    @Override
    public void sendEmail(User user) {
        try {
            Context context = new Context();
            context.setVariable("user", user);

            String process = templateEngine.process("emails/welcome", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setSubject("Welcome " + user.getUsername());
            helper.setText(process, true);
            helper.setTo(user.getEmail());
            helper.setFrom(new InternetAddress(senderEmail, senderText));
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            String message = "Wrong email data";
            log.error(message);
            throw new BadRequestException(message);
        }
    }
}
