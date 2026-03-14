package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:noreply@pickados.com}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void sendHtmlEmail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true indicates HTML format

            javaMailSender.send(message);
            logger.info("Sent email to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }
}
