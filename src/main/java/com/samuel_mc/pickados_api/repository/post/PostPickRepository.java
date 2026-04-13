package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostPickEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostPickRepository extends JpaRepository<PostPickEntity, Long> {
    Optional<PostPickEntity> findByPostId(Long postId);
}
