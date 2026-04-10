package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.SportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportRepository extends JpaRepository<SportEntity, Long> {
    Optional<SportEntity> findByNameIgnoreCase(String name);
}
