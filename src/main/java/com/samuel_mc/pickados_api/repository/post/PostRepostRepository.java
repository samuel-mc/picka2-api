package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostRepostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepostRepository extends JpaRepository<PostRepostEntity, Long> {
    long countByPostId(Long postId);
    Optional<PostRepostEntity> findByPostIdAndUserId(Long postId, Long userId);
}
