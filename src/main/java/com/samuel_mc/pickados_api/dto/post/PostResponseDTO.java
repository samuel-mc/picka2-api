package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.PostType;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    private Long id;
    private PostType type;
    private String content;
    private PostVisibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime parleyEventDate;
    private String timelineEntryId;
    private Boolean repostEntry;
    private LocalDateTime repostedAt;
    private PostAuthorResponseDTO author;
    private PostAuthorResponseDTO repostedBy;
    private List<String> mediaUrls;
    private List<String> tags;
    private PostPickResponseDTO simplePick;
    private PostParleyResponseDTO parley;
    private List<ParleySelectionResponseDTO> parleySelections;
    private PostMetricsResponseDTO metrics;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
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

    public LocalDateTime getParleyEventDate() {
        return parleyEventDate;
    }

    public void setParleyEventDate(LocalDateTime parleyEventDate) {
        this.parleyEventDate = parleyEventDate;
    }

    public String getTimelineEntryId() {
        return timelineEntryId;
    }

    public void setTimelineEntryId(String timelineEntryId) {
        this.timelineEntryId = timelineEntryId;
    }

    public Boolean getRepostEntry() {
        return repostEntry;
    }

    public void setRepostEntry(Boolean repostEntry) {
        this.repostEntry = repostEntry;
    }

    public LocalDateTime getRepostedAt() {
        return repostedAt;
    }

    public void setRepostedAt(LocalDateTime repostedAt) {
        this.repostedAt = repostedAt;
    }

    public PostAuthorResponseDTO getAuthor() {
        return author;
    }

    public void setAuthor(PostAuthorResponseDTO author) {
        this.author = author;
    }

    public PostAuthorResponseDTO getRepostedBy() {
        return repostedBy;
    }

    public void setRepostedBy(PostAuthorResponseDTO repostedBy) {
        this.repostedBy = repostedBy;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public PostPickResponseDTO getSimplePick() {
        return simplePick;
    }

    public void setSimplePick(PostPickResponseDTO simplePick) {
        this.simplePick = simplePick;
    }

    public List<ParleySelectionResponseDTO> getParleySelections() {
        return parleySelections;
    }

    public void setParleySelections(List<ParleySelectionResponseDTO> parleySelections) {
        this.parleySelections = parleySelections;
    }

    public PostParleyResponseDTO getParley() {
        return parley;
    }

    public void setParley(PostParleyResponseDTO parley) {
        this.parley = parley;
    }

    public PostMetricsResponseDTO getMetrics() {
        return metrics;
    }

    public void setMetrics(PostMetricsResponseDTO metrics) {
        this.metrics = metrics;
    }
}
