package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.ReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferralRepository extends JpaRepository<ReferralEntity, Long> {
    Optional<ReferralEntity> findByInvitedId(Long invitedUserId);
}

