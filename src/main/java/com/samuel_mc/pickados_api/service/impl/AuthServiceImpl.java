package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.dto.RegisterRequest;
import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.exception.GenericException;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.service.AuthService;
import com.samuel_mc.pickados_api.util.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void userRegister(RegisterRequest req) {
        try {
            this.validateUserRegister(req);

            UserEntity user = new UserEntity();
            user.setName(req.getName());
            user.setUsername(req.getUsername());
            user.setEmail(req.getEmail());
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            // asigna role USER por defecto
            RoleEntity role = roleRepository.findByName("USER").orElseGet(() -> {
                RoleEntity r = new RoleEntity();
                r.setName("USER");
                return roleRepository.save(r);
            });
            user.setRole(role);
            userRepository.save(user);
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al registrar el usuario: {}", String.valueOf(e));
            throw new GenericException("403", "Error al registrar el usuario");
        }
    }

    @Override
    public void tipsterRegister() {

    }


    private void validateUserRegister(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new GenericException(ResponseCode.FORBIDDEN.getCode(), "username ya existe");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new GenericException(ResponseCode.FORBIDDEN.getCode(), "email ya existe");
        }
    }
}
