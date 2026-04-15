package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;

public interface PostViewRepository extends JpaRepository<PostViewEntity, Long> {
    long countByPostId(Long postId);
    Optional<PostViewEntity> findByPostIdAndUserId(Long postId, Long userId);
    long countByViewedAtAfter(LocalDateTime viewedAt);
}
