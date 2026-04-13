package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.HomePrasheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HomePrasheRepository extends JpaRepository<HomePrasheEntity, Long> {
    Optional<HomePrasheEntity> findByNameIgnoreCase(String name);
}
