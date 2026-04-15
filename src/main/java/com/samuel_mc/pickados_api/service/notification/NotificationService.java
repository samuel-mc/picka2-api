package com.samuel_mc.pickados_api.service.notification;

import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.dto.notification.NotificationActorResponseDTO;
import com.samuel_mc.pickados_api.dto.notification.NotificationItemResponseDTO;
import com.samuel_mc.pickados_api.dto.notification.NotificationListResponseDTO;
import com.samuel_mc.pickados_api.entity.CommentEntity;
import com.samuel_mc.pickados_api.entity.NotificationEntity;
import com.samuel_mc.pickados_api.entity.PostEntity;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.entity.enums.NotificationType;
import com.samuel_mc.pickados_api.repository.NotificationRepository;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final R2Properties r2Properties;

    public NotificationService(
            NotificationRepository notificationRepository,
            TipsterProfileRepository tipsterProfileRepository,
            R2Properties r2Properties
    ) {
        this.notificationRepository = notificationRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.r2Properties = r2Properties;
    }

    @Transactional
    public void notifyFollowStarted(UserEntity actor, UserEntity recipient) {
        createNotification(actor, recipient, NotificationType.FOLLOW_STARTED, null, null);
    }

    @Transactional
    public void notifyPostComment(UserEntity actor, PostEntity post, CommentEntity comment) {
        createNotification(actor, post.getAuthor(), NotificationType.POST_COMMENT, post, comment);
    }

    @Transactional
    public void notifyPostLike(UserEntity actor, PostEntity post) {
        createNotification(actor, post.getAuthor(), NotificationType.POST_LIKE, post, null);
    }

    @Transactional
    public void notifyCommentLike(UserEntity actor, CommentEntity comment) {
        createNotification(actor, comment.getAuthor(), NotificationType.COMMENT_LIKE, comment.getPost(), comment);
    }

    @Transactional
    public NotificationListResponseDTO getNotifications(Long currentUserId) {
        List<NotificationEntity> entities = notificationRepository.findTop50ByRecipientIdOrderByCreatedAtDesc(currentUserId);
        NotificationListResponseDTO dto = new NotificationListResponseDTO();
        dto.setUnreadCount(notificationRepository.countByRecipientIdAndReadAtIsNull(currentUserId));

        List<NotificationItemResponseDTO> items = new ArrayList<>();
        Set<Long> consumedIds = new HashSet<>();

        for (NotificationEntity entity : entities) {
            if (consumedIds.contains(entity.getId())) {
                continue;
            }

            if (entity.getReadAt() == null
                    && entity.getType() == NotificationType.POST_LIKE
                    && entity.getPost() != null) {
                List<NotificationEntity> grouped = entities.stream()
                        .filter(candidate -> !consumedIds.contains(candidate.getId()))
                        .filter(candidate -> candidate.getReadAt() == null)
                        .filter(candidate -> candidate.getType() == NotificationType.POST_LIKE)
                        .filter(candidate -> candidate.getPost() != null)
                        .filter(candidate -> candidate.getPost().getId().equals(entity.getPost().getId()))
                        .toList();
                grouped.forEach(candidate -> consumedIds.add(candidate.getId()));
                items.add(mapNotification(grouped.get(0), grouped.size() - 1));
                continue;
            }

            consumedIds.add(entity.getId());
            items.add(mapNotification(entity, 0));
        }

        dto.setItems(items);
        return dto;
    }

    @Transactional
    public void markAsRead(Long currentUserId, Long notificationId) {
        NotificationEntity entity = notificationRepository.findByIdAndRecipientId(notificationId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Notificación no encontrada"));

        LocalDateTime now = LocalDateTime.now();
        if (entity.getType() == NotificationType.POST_LIKE && entity.getPost() != null) {
            List<NotificationEntity> grouped = notificationRepository
                    .findByRecipientIdAndReadAtIsNullAndTypeAndPost_Id(
                            currentUserId,
                            NotificationType.POST_LIKE,
                            entity.getPost().getId()
                    );
            for (NotificationEntity notification : grouped) {
                notification.setReadAt(now);
            }
            notificationRepository.saveAll(grouped);
            return;
        }

        entity.setReadAt(now);
        notificationRepository.save(entity);
    }

    private void createNotification(
            UserEntity actor,
            UserEntity recipient,
            NotificationType type,
            PostEntity post,
            CommentEntity comment
    ) {
        if (actor == null || recipient == null || actor.getId() == recipient.getId()) {
            return;
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setActor(actor);
        entity.setRecipient(recipient);
        entity.setType(type);
        entity.setPost(post);
        entity.setComment(comment);
        notificationRepository.save(entity);
    }

    private NotificationItemResponseDTO mapNotification(NotificationEntity entity, int extraActorsCount) {
        NotificationItemResponseDTO dto = new NotificationItemResponseDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setRead(entity.getReadAt() != null);
        dto.setExtraActorsCount(extraActorsCount);
        dto.setPostId(entity.getPost() != null ? entity.getPost().getId() : null);
        dto.setCommentId(entity.getComment() != null ? entity.getComment().getId() : null);
        dto.setTargetUserId(entity.getType() == NotificationType.FOLLOW_STARTED ? entity.getActor().getId() : null);
        dto.setActor(mapActor(entity.getActor()));
        dto.setMessage(buildMessage(entity, extraActorsCount));
        return dto;
    }

    private String buildMessage(NotificationEntity entity, int extraActorsCount) {
        String actorName = entity.getActor().getName();
        return switch (entity.getType()) {
            case FOLLOW_STARTED -> actorName + " te comenzó a seguir";
            case POST_COMMENT -> actorName + " comentó tu post";
            case POST_LIKE -> extraActorsCount > 0
                    ? actorName + " y " + extraActorsCount + " más le dieron like a tu post"
                    : actorName + " le dio like a tu post";
            case COMMENT_LIKE -> actorName + " le dio me gusta a tu comentario";
        };
    }

    private NotificationActorResponseDTO mapActor(UserEntity user) {
        NotificationActorResponseDTO dto = new NotificationActorResponseDTO();
        dto.setId(user.getId());
        dto.setName((user.getName() + " " + user.getLastname()).trim());
        dto.setUsername(user.getUsername());

        TipsterProfileEntity tipsterProfile = tipsterProfileRepository.findByUser(user).orElse(null);
        String avatarKey = tipsterProfile != null && tipsterProfile.getAvatarUrl() != null && !tipsterProfile.getAvatarUrl().isBlank()
                ? tipsterProfile.getAvatarUrl()
                : user.getProfilePhotoKey();
        dto.setAvatarUrl(resolvePublicUrl(avatarKey));

        boolean validated = tipsterProfile != null && Boolean.TRUE.equals(tipsterProfile.getValidated());
        dto.setValidatedTipster(validated);
        dto.setBadge(validated ? "Verified Tipster" : null);
        return dto;
    }

    private String resolvePublicUrl(String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            return null;
        }
        if (storedValue.startsWith("http://") || storedValue.startsWith("https://")) {
            return storedValue;
        }
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            return storedValue;
        }
        return base.replaceAll("/$", "") + "/" + storedValue;
    }
}
