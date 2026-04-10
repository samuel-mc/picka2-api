package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.CompetitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<CompetitionEntity, Long> {
    Optional<CompetitionEntity> findByNameIgnoreCaseAndSport_IdAndCountry_Id(String name, Long sportId, Long countryId);

    long countBySport_Id(Long sportId);

    long countByCountry_Id(Long countryId);
}
