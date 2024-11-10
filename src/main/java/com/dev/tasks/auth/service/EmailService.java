package com.dev.tasks.auth.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.Future;

@Service
@Log
public class EmailService {

    @Value("${client.url}")
    private String clientURL;

    @Value("${email.from}")
    private String emailFrom;

    private final JavaMailSender sender;

    @Autowired
    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    @Async
    public Future<Boolean> sendActivationEmail(String email, String username, String uuid) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");

            String url = clientURL + "/activate-account/" + uuid;

            String htmlMsg = String.format(
                    "Hello.<br/><br/>" +
                            "You have created an account for the web application. \"Task planner\": %s <br/><br/>" +
                            "<a href='%s'>%s</a><br/><br/>", username, url, "To confirm your registration, click on this link");

            mimeMessage.setContent(htmlMsg, "text/html");

            message.setTo(email);
            message.setFrom(emailFrom);
            message.setSubject("Account activation required");
            message.setText(htmlMsg, true);
            sender.send(mimeMessage);

            return new AsyncResult<>(true);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

    @Async
    public Future<Boolean> sendResetPasswordEmail(String email, String token) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");

            String url = clientURL + "/update-password/" + token;

            String htmlMsg = String.format(
                    "Hello.<br/><br/>" +
                            "Someone requested a password reset for the web application \"Task planner\".<br/><br/>" +
                            "If it wasn't you, just delete this email..<br/><br/> Click the link below if you want to reset your password: <br/><br/> " +
                            "<a href='%s'>%s</a><br/><br/>", url, "Reset password");

            mimeMessage.setContent(htmlMsg, "text/html");

            message.setTo(email);
            message.setSubject("Reset password");
            message.setFrom(emailFrom);
            message.setText(htmlMsg, true);
            sender.send(mimeMessage);

            return new AsyncResult<>(true);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }
}
