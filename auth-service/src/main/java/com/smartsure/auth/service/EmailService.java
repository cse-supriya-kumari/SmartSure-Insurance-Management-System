package com.smartsure.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendWelcomeEmail(String toName, String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@smartsure.com"); // Matches configured username domain usually
            message.setTo(toEmail);
            message.setSubject("Welcome to SmartSure Insurance");
            message.setText("Hello " + toName + ",\n\n" +
                    "Welcome to SmartSure Insurance.\n\n" +
                    "We are thrilled to have you on board. You can now log in and browse our comprehensive policy offerings.\n\n\n" +
                    "Best Regards,\n\n" +
                    "SmartSure Team");

            javaMailSender.send(message);
            logger.info("Welcome email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}", toEmail, e);
        }
    }
}
