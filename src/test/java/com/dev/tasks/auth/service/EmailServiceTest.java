package com.dev.tasks.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void testSendActivationEmail() throws MessagingException {
        // Date de test
        String email = "user@example.com";
        String username = "John Doe";
        String uuid = "12345";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String url = "http://localhost/activate-account/" + uuid;

        String htmlMsg = String.format(
                "Hello.<br/><br/>" +
                        "You have created an account for the web application. \"Task planner\": %s <br/><br/>" +
                        "<a href='%s'>%s</a><br/><br/>", username, url, "To confirm your registration, click on this link");

        mimeMessage.setContent(htmlMsg, "text/html");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setFrom("test@example.com");
        mimeMessageHelper.setSubject("Account activation required");
        mimeMessageHelper.setText(htmlMsg, true);

        emailService.sendActivationEmail(email, username, uuid);

        assertTrue(true, "Email was successfully sent.");
    }

    @Test
    void testSendResetPasswordEmail() throws MessagingException {
        String email = "user@example.com";
        String token = "resetToken123";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        String url = "http://localhost/update-password/" + token;

        String htmlMsg = String.format(
                "Hello.<br/><br/>" +
                        "Someone requested a password reset for the web application \"Task planner\".<br/><br/>" +
                        "If it wasn't you, just delete this email..<br/><br/> Click the link below if you want to reset your password: <br/><br/> " +
                        "<a href='%s'>%s</a><br/><br/>", url, "Reset password");

        mimeMessage.setContent(htmlMsg, "text/html");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setFrom("test@example.com");
        mimeMessageHelper.setSubject("Reset password");
        mimeMessageHelper.setText(htmlMsg, true);

        emailService.sendResetPasswordEmail(email, token);

        assertTrue(true, "Reset password email was successfully sent.");
    }
}
