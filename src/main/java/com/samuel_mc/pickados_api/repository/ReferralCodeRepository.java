package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.ReferralCodeEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralCodeRepository extends JpaRepository<ReferralCodeEntity, Long> {
    Optional<ReferralCodeEntity> findByUser(UserEntity user);

    Optional<ReferralCodeEntity> findByCode(String code);
}

