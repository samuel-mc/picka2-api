package com.samuel_mc.pickados_api.service.strategy;

import com.samuel_mc.pickados_api.dto.RegisterAdminRequestDTO;
import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;
import com.samuel_mc.pickados_api.dto.mappers.UserMapper;
import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminRegistrationStrategy implements UserRegistrationStrategy {

    private static final String ROLE_ADMIN = "ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminRegistrationStrategy(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterUserRequestDTO req) {
        RegisterAdminRequestDTO adminReq = (RegisterAdminRequestDTO) req;
        UserEntity userEntity = UserMapper.INSTANCIA.registerUserRequestDTOToUserEntity(adminReq);
        userEntity.setPassword(passwordEncoder.encode(adminReq.getPassword()));
        userEntity.setActive(true);
        userEntity.setDeleted(false);

        RoleEntity role = roleRepository.findByName(ROLE_ADMIN).orElseGet(() -> {
            RoleEntity r = new RoleEntity();
            r.setName(ROLE_ADMIN);
            return roleRepository.save(r);
        });
        userEntity.setRole(role);

        userRepository.save(userEntity);
    }

    @Override
    public boolean supports(Class<? extends RegisterUserRequestDTO> requestClass) {
        return RegisterAdminRequestDTO.class.equals(requestClass);
    }
}
