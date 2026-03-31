package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipsterProfileRepository extends JpaRepository<TipsterProfileEntity, Long> {
    Optional<TipsterProfileEntity> findByUser(UserEntity user);
}
