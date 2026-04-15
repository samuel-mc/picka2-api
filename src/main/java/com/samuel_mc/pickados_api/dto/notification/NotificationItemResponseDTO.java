package com.samuel_mc.pickados_api.dto.notification;

import com.samuel_mc.pickados_api.entity.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationItemResponseDTO {
    private Long id;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
    private int extraActorsCount;
    private Long postId;
    private Long commentId;
    private Long targetUserId;
    private NotificationActorResponseDTO actor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public int getExtraActorsCount() {
        return extraActorsCount;
    }

    public void setExtraActorsCount(int extraActorsCount) {
        this.extraActorsCount = extraActorsCount;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public NotificationActorResponseDTO getActor() {
        return actor;
    }

    public void setActor(NotificationActorResponseDTO actor) {
        this.actor = actor;
    }
}
