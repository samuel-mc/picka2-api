package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostParleyEntity;
import com.samuel_mc.pickados_api.entity.enums.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PostParleyRepository extends JpaRepository<PostParleyEntity, Long> {
    long countByResultStatus(ResultStatus resultStatus);
    long countByResultStatusNot(ResultStatus resultStatus);
    long countByResultStatusNotAndUpdatedAtAfter(ResultStatus resultStatus, LocalDateTime updatedAt);
}
