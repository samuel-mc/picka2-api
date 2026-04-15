package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    long countByCommentId(Long commentId);
    Optional<CommentLikeEntity> findByCommentIdAndUserId(Long commentId, Long userId);
}
