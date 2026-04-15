package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.NotificationEntity;
import com.samuel_mc.pickados_api.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findTop50ByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    long countByRecipientIdAndReadAtIsNull(Long recipientId);
    Optional<NotificationEntity> findByIdAndRecipientId(Long notificationId, Long recipientId);
    List<NotificationEntity> findByRecipientIdAndReadAtIsNullAndTypeAndPost_Id(Long recipientId, NotificationType type, Long postId);
}
