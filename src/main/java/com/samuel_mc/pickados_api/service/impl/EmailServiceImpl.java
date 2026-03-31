package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final ResourceLoader resourceLoader;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender javaMailSender, ResourceLoader resourceLoader) {
        this.javaMailSender = javaMailSender;
        this.resourceLoader = resourceLoader;
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

            try {
                Resource logoResource = resourceLoader.getResource("classpath:static/logo.png");
                if (logoResource.exists()) {
                    helper.addInline("logoImage", logoResource);
                }
            } catch (Exception e) {
                logger.warn("No se pudo adjuntar el logo embebido: {}", e.getMessage());
            }

            javaMailSender.send(message);
            logger.info("Sent email to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }

    @Override
    @Async
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, String> context) {
        try {
            Resource resource = resourceLoader.getResource("classpath:templates/" + templateName);
            String htmlTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : context.entrySet()) {
                htmlTemplate = htmlTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            sendHtmlEmail(to, subject, htmlTemplate);
        } catch (IOException e) {
            logger.error("Failed to load email template: {}", templateName, e);
        }
    }
}
