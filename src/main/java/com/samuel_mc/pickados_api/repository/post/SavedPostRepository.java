package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.SavedPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;

public interface SavedPostRepository extends JpaRepository<SavedPostEntity, Long> {
    long countByPostId(Long postId);
    Optional<SavedPostEntity> findByPostIdAndUserId(Long postId, Long userId);
    long countByCreatedAtAfter(LocalDateTime createdAt);
}
