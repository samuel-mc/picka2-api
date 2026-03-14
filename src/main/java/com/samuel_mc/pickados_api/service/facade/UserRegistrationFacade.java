package com.samuel_mc.pickados_api.service.facade;

import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.exception.GenericException;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.service.strategy.UserRegistrationStrategy;
import com.samuel_mc.pickados_api.util.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRegistrationFacade {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationFacade.class);

    private final List<UserRegistrationStrategy> registrationStrategies;
    private final UserRepository userRepository;

    public UserRegistrationFacade(List<UserRegistrationStrategy> registrationStrategies,
            UserRepository userRepository) {
        this.registrationStrategies = registrationStrategies;
        this.userRepository = userRepository;
    }

    public void processRegistration(RegisterUserRequestDTO req) {
        try {
            validateUserRegister(req);

            UserRegistrationStrategy strategy = registrationStrategies.stream()
                    .filter(s -> s.supports(req.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No strategy found for registration type: " + req.getClass().getSimpleName()));

            strategy.register(req);
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar el usuario: {}", e.getMessage(), e);
            throw new GenericException("403", "Error al registrar el usuario");
        }
    }

    private void validateUserRegister(RegisterUserRequestDTO req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new GenericException(ResponseCode.FORBIDDEN.getCode(),
                    "El username ya se encuentra registrado, prueba con uno diferente.");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new GenericException(ResponseCode.FORBIDDEN.getCode(),
                    "El email ya se encuentra registrado, prueba con uno diferente.");
        }
    }
}
