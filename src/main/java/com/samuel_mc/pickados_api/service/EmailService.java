package com.samuel_mc.pickados_api.service;

import java.util.Map;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String text);
    void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, String> context);
}
