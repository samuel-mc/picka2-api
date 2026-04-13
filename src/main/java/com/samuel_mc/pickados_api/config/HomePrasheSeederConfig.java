package com.samuel_mc.pickados_api.config;

import com.samuel_mc.pickados_api.entity.HomePrasheEntity;
import com.samuel_mc.pickados_api.repository.HomePrasheRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HomePrasheSeederConfig {

    @Bean
    CommandLineRunner homePrasheSeeder(HomePrasheRepository homePrasheRepository) {
        return args -> {
            String defaultPhrase = "Publica picks, analisis y parleys con estructura lista para crecer.";
            homePrasheRepository.findByNameIgnoreCase(defaultPhrase).orElseGet(() -> {
                HomePrasheEntity entity = new HomePrasheEntity();
                entity.setName(defaultPhrase);
                entity.setActive(true);
                return homePrasheRepository.save(entity);
            });
        };
    }
}
