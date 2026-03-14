package com.samuel_mc.pickados_api.service.strategy;

import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.dto.mappers.UserMapper;
import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StandardUserRegistrationStrategy implements UserRegistrationStrategy {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StandardUserRegistrationStrategy(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterUserRequestDTO req) {
        UserEntity userEntity = UserMapper.INSTANCIA.registerUserRequestDTOToUserEntity(req);
        userEntity.setPassword(passwordEncoder.encode(req.getPassword()));

        RoleEntity role = roleRepository.findByName("ADMIN").orElseGet(() -> {
            RoleEntity r = new RoleEntity();
            r.setName("ADMIN");
            return roleRepository.save(r);
        });
        userEntity.setRole(role);

        userRepository.save(userEntity);
    }

    @Override
    public boolean supports(Class<? extends RegisterUserRequestDTO> requestClass) {
        return RegisterUserRequestDTO.class.equals(requestClass);
    }
}
