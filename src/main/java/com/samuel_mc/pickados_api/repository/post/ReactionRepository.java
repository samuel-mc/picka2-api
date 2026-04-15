package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.ReactionEntity;
import com.samuel_mc.pickados_api.entity.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;

public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {
    long countByPostIdAndType(Long postId, ReactionType type);
    Optional<ReactionEntity> findByPostIdAndUserId(Long postId, Long userId);
    long countByType(ReactionType type);
    long countByCreatedAtAfter(LocalDateTime createdAt);
}
