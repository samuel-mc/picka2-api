package com.samuel_mc.pickados_api.service;

public interface EmailVerificationService {
    void verifyEmail(String token);
}
