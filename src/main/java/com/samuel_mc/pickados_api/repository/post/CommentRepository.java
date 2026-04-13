package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    long countByPostId(Long postId);
    List<CommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);
}
