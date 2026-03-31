package com.samuel_mc.pickados_api.service.strategy;

import com.samuel_mc.pickados_api.dto.RegisterTipsterRequestDTO;
import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.dto.mappers.UserMapper;
import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.samuel_mc.pickados_api.util.JwtUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * Estrategia de registro para usuarios de tipo Tipster.
 *
 * Se encarga de:
 * 1. Mapear el request a la entidad de usuario.
 * 2. Encriptar la contraseña antes de persistir.
 * 3. Asignar el rol base "USER".
 * 4. Crear el perfil específico de tipster.
 * 5. Enviar un correo de bienvenida al finalizar el registro.
 */
@Component
public class TipsterRegistrationStrategy implements UserRegistrationStrategy {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public TipsterRegistrationStrategy(UserRepository userRepository, RoleRepository roleRepository,
            TipsterProfileRepository tipsterProfileRepository, PasswordEncoder passwordEncoder, EmailService emailService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registra un nuevo usuario tipster dentro de una transacción.
     *
     * El flujo asegura que tanto el usuario como su perfil de tipster
     * se creen de forma consistente. Si ocurre un error durante el proceso,
     * la transacción revierte los cambios en base de datos.
     *
     * @param req DTO genérico de registro que se castea a RegisterTipsterRequestDTO.
     */
    @Override
    @Transactional
    public void register(RegisterUserRequestDTO req) {
        // Se convierte el DTO genérico al tipo específico requerido para tipsters.
        RegisterTipsterRequestDTO tipsterReq = (RegisterTipsterRequestDTO) req;

        // Se mapean los datos generales del usuario desde el request hacia la entidad.
        UserEntity userEntity = UserMapper.INSTANCIA.registerUserRequestDTOToUserEntity(tipsterReq);
        // La contraseña se almacena encriptada para no persistir texto plano.
        userEntity.setPassword(passwordEncoder.encode(tipsterReq.getPassword()));

        // Se busca el rol base del sistema; si no existe, se crea en ese momento.
        RoleEntity role = roleRepository.findByName("TIPSTER").orElseGet(() -> {
            RoleEntity r = new RoleEntity();
            r.setName("TIPSTER");
            return roleRepository.save(r);
        });
        // Se asigna el rol recuperado o creado al nuevo usuario.
        userEntity.setRole(role);

        userEntity.setActive(true);
        userEntity.setDeleted(false);

        // Primero se guarda el usuario para contar con su identificador persistido.
        userEntity = userRepository.save(userEntity);

        // Se construye el perfil específico del tipster con los datos complementarios.
        TipsterProfileEntity tipster = UserMapper.INSTANCIA.registerTipsterRequestDTOtoTipsterProfileEntity(tipsterReq);
        // Se vincula el perfil de tipster con el usuario recién creado.
        tipster.setUser(userEntity);

        tipster.setValidated(false);

        // Se persiste el perfil del tipster en la base de datos.
        tipsterProfileRepository.save(tipster);

        // Se prepara el contenido del correo de bienvenida posterior al registro.
        String token = jwtUtil.generateEmailVerificationToken(tipsterReq.getEmail());
        String subject = "¡Bienvenido a Pickados!";
        java.util.Map<String, String> context = new java.util.HashMap<>();
        context.put("nombreUsuario", tipsterReq.getName());
        context.put("confirmacionUrl", frontendUrl + "/auth/verify-email?token=" + token); 
        context.put("tiempoExpiracion", "24 horas");
        context.put("anio", String.valueOf(java.time.Year.now().getValue()));
        
        // Se envía un correo HTML de confirmación al email proporcionado por el usuario.
        emailService.sendEmailWithTemplate(tipsterReq.getEmail(), subject, "email-confirm.html", context);
    }

    /**
     * Indica si esta estrategia soporta el tipo de request recibido.
     *
     * @param requestClass clase concreta del DTO de registro.
     * @return true cuando el request corresponde a RegisterTipsterRequestDTO.
     */
    @Override
    public boolean supports(Class<? extends RegisterUserRequestDTO> requestClass) {
        return RegisterTipsterRequestDTO.class.equals(requestClass);
    }
}
