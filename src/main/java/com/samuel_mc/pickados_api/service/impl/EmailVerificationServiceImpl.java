package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.service.EmailService;
import com.samuel_mc.pickados_api.service.EmailVerificationService;
import com.samuel_mc.pickados_api.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationServiceImpl.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailVerificationServiceImpl(JwtUtil jwtUtil, UserRepository userRepository,
                                        TipsterProfileRepository tipsterProfileRepository, EmailService emailService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        if (!jwtUtil.validateTokenForPurpose(token, JwtUtil.EMAIL_VERIFICATION_PURPOSE)) {
            throw new IllegalArgumentException("Token de verificación inválido o expirado");
        }

        String email = jwtUtil.getEmailFromToken(token);
        Optional<UserEntity> userOpt = userRepository.findByEmailAndDeletedFalse(email);

        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con el email provisto");
        }

        UserEntity user = userOpt.get();

        // Si es Tipster, validamos su perfil
        if ("TIPSTER".equals(user.getRole().getName())) {
            Optional<TipsterProfileEntity> profileOpt = tipsterProfileRepository.findByUser(user);
            if (profileOpt.isPresent()) {
                TipsterProfileEntity profile = profileOpt.get();
                if (Boolean.TRUE.equals(profile.getValidated())) {
                    logger.info("El correo {} ya estaba validado", email);
                    return;
                }
                profile.setValidated(true);
                tipsterProfileRepository.save(profile);
            }
        }
        
        // Aquí podríamos también agregar lógica de validación general para 'USER' si se requiere en el futuro.
        
        // Enviar correo de éxito
        sendSuccessEmail(user);
    }

    private void sendSuccessEmail(UserEntity user) {
        String subject = "¡Correo confirmado!";
        Map<String, String> context = new HashMap<>();
        context.put("nombreUsuario", user.getName());
        context.put("loginUrl", frontendUrl + "/login");
        context.put("anio", String.valueOf(Year.now().getValue()));

        emailService.sendEmailWithTemplate(user.getEmail(), subject, "email-confirmed.html", context);
    }
}
