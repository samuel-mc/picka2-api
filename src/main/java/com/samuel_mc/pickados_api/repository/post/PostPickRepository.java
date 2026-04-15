package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostPickEntity;
import com.samuel_mc.pickados_api.entity.enums.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostPickRepository extends JpaRepository<PostPickEntity, Long> {
    Optional<PostPickEntity> findByPostId(Long postId);
    long countByResultStatus(ResultStatus resultStatus);
    long countByResultStatusNot(ResultStatus resultStatus);
    long countByResultStatusNotAndUpdatedAtAfter(ResultStatus resultStatus, LocalDateTime updatedAt);
}
