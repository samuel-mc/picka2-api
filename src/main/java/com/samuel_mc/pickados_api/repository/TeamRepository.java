package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    Optional<TeamEntity> findByNameIgnoreCaseAndCompetition_Id(String name, Long competitionId);

    long countByCompetition_Id(Long competitionId);
}
