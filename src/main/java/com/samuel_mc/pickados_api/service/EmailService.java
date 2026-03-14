package com.samuel_mc.pickados_api.service;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String text);
}
