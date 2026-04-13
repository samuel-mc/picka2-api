package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostShareRepository extends JpaRepository<PostShareEntity, Long> {
    long countByPostId(Long postId);
}
