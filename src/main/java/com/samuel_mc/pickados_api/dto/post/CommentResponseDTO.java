package com.samuel_mc.pickados_api.dto.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentResponseDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PostAuthorResponseDTO author;
    private Long parentCommentId;
    private String replyingToUsername;
    private long likesCount;
    private boolean likedByCurrentUser;
    private List<CommentResponseDTO> replies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PostAuthorResponseDTO getAuthor() {
        return author;
    }

    public void setAuthor(PostAuthorResponseDTO author) {
        this.author = author;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getReplyingToUsername() {
        return replyingToUsername;
    }

    public void setReplyingToUsername(String replyingToUsername) {
        this.replyingToUsername = replyingToUsername;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public List<CommentResponseDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentResponseDTO> replies) {
        this.replies = replies;
    }
}
