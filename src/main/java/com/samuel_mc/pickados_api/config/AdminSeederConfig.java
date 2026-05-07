package com.samuel_mc.pickados_api.config;

import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
public class AdminSeederConfig {

    @Bean
    public ApplicationRunner seedAdminUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "admin@pickados.local";
            String username = "admin";

            if (userRepository.findByEmail(email).isPresent() || userRepository.existsByUsername(username)) {
                return;
            }

            RoleEntity adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                RoleEntity role = new RoleEntity();
                role.setName("ADMIN");
                return roleRepository.save(role);
            });

            UserEntity admin = new UserEntity();
            admin.setName("Admin");
            admin.setLastname("Seed");
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setActive(true);
            admin.setDeleted(false);
            admin.setRole(adminRole);

            userRepository.save(admin);
        };
    }

    @Bean
    public ApplicationRunner seedSpriteTestUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "john1237@pickados.local";
            String username = "john1237";

            if (userRepository.findByEmail(email).isPresent() || userRepository.existsByUsername(username)) {
                return;
            }

            RoleEntity userRole = roleRepository.findByName("USER").orElseGet(() -> {
                RoleEntity role = new RoleEntity();
                role.setName("USER");
                return roleRepository.save(role);
            });

            UserEntity user = new UserEntity();
            user.setName("John");
            user.setLastname("SpriteTest");
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setActive(true);
            user.setDeleted(false);
            user.setRole(userRole);

            userRepository.save(user);
        };
    }
}

