package com.samuel_mc.pickados_api.config;

import com.samuel_mc.pickados_api.entity.SportsbookEntity;
import com.samuel_mc.pickados_api.repository.post.SportsbookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SportsbookSeederConfig {

    @Bean
    CommandLineRunner sportsbookSeeder(SportsbookRepository sportsbookRepository) {
        return args -> {
            List<SportsbookSeed> seeds = List.of(
                    new SportsbookSeed("Bet365", "https://www.bet365.com"),
                    new SportsbookSeed("Caliente", "https://www.caliente.mx"),
                    new SportsbookSeed("Codere", "https://www.codere.mx"),
                    new SportsbookSeed("1xBet", "https://1xbet.com"),
                    new SportsbookSeed("Pinnacle", "https://www.pinnacle.com"),
                    new SportsbookSeed("Betway", "https://www.betway.com"),
                    new SportsbookSeed("DraftKings", "https://www.draftkings.com"),
                    new SportsbookSeed("FanDuel", "https://www.fanduel.com")
            );

            for (SportsbookSeed seed : seeds) {
                sportsbookRepository.findByNameIgnoreCase(seed.name()).orElseGet(() -> {
                    SportsbookEntity entity = new SportsbookEntity();
                    entity.setName(seed.name());
                    entity.setBaseUrl(seed.baseUrl());
                    entity.setActive(true);
                    return sportsbookRepository.save(entity);
                });
            }
        };
    }

    private record SportsbookSeed(String name, String baseUrl) {
    }
}
