package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.auth.User;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {
    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ITemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendEmail_checkMethodInvocation() {
        String username = "user";
        String email = "user@gmail.com";
        String process = "some text";
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn(process);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(user);

        verify(javaMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_checkThrowExceptionInvocation() {
        String username = "user";
        String email = "user@gmail.com";
        String process = "some text";
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn(process);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException()).when(javaMailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> emailService.sendEmail(user))
                .hasMessage("Sending email was interrupted");
    }
}
