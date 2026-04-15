package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostShareRepository extends JpaRepository<PostShareEntity, Long> {
    long countByPostId(Long postId);
    long countByPostIdAndUserIdNot(Long postId, Long userId);
    long countByCreatedAtAfter(LocalDateTime createdAt);
}
