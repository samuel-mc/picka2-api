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

    public TipsterRegistrationStrategy(UserRepository userRepository, RoleRepository roleRepository,
            TipsterProfileRepository tipsterProfileRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
            r.setName("USER");
            return roleRepository.save(r);
        });
        // Se asigna el rol recuperado o creado al nuevo usuario.
        userEntity.setRole(role);

        // Primero se guarda el usuario para contar con su identificador persistido.
        userEntity = userRepository.save(userEntity);

        // Se construye el perfil específico del tipster con los datos complementarios.
        TipsterProfileEntity tipster = UserMapper.INSTANCIA.registerTipsterRequestDTOtoTipsterProfileEntity(tipsterReq);
        // Se vincula el perfil de tipster con el usuario recién creado.
        tipster.setUser(userEntity);

        // Se persiste el perfil del tipster en la base de datos.
        tipsterProfileRepository.save(tipster);

        // Se prepara el contenido del correo de bienvenida posterior al registro.
//        String subject = "¡Bienvenido a Pickados!";
//        String text = "<h1>Hola, " + tipsterReq.getName() + "</h1>" +
//                "<p>Tu registro como Tipster ha sido exitoso.</p>" +
//                "<p>¡Gracias por unirte a nosotros!</p>";
        // Se envía un correo HTML de confirmación al email proporcionado por el usuario.
//        emailService.sendHtmlEmail(tipsterReq.getEmail(), subject, text);
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
