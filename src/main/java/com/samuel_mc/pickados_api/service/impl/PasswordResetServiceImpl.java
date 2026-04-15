package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.service.EmailService;
import com.samuel_mc.pickados_api.service.PasswordResetService;
import com.samuel_mc.pickados_api.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public PasswordResetServiceImpl(UserRepository userRepository, EmailService emailService, JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void requestPasswordReset(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmailAndDeletedFalse(email)
                .filter(user -> Boolean.TRUE.equals(user.getActive()));

        if (userOpt.isEmpty()) {
            logger.info("Se solicitó recuperación de contraseña para un email no registrado: {}", email);
            return;
        }

        UserEntity user = userOpt.get();
        String token = jwtUtil.generatePasswordResetToken(user.getEmail());

        Map<String, String> context = new HashMap<>();
        context.put("nombreUsuario", user.getName());
        context.put("restablecerUrl", frontendUrl + "/reset-password?token=" + token);
        context.put("tiempoExpiracion", "60 minutos");
        context.put("anio", String.valueOf(Year.now().getValue()));

        emailService.sendEmailWithTemplate(
                user.getEmail(),
                "Actualiza tu contraseña - Pickados",
                "password-reset.html",
                context);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (!jwtUtil.validateTokenForPurpose(token, JwtUtil.PASSWORD_RESET_PURPOSE)) {
            throw new IllegalArgumentException("Token de recuperación inválido o expirado");
        }

        String email = jwtUtil.getEmailFromToken(token);
        UserEntity user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con el email provisto"));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException("Usuario no disponible para restablecimiento");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
