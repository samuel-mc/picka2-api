package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.ReactionType;

public class PostMetricsResponseDTO {
    private long commentsCount;
    private long likesCount;
    private long dislikesCount;
    private long savesCount;
    private long viewsCount;
    private long sharesCount;
    private long repostsCount;
    private ReactionType currentUserReaction;
    private boolean savedByCurrentUser;
    private boolean repostedByCurrentUser;

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(long dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public long getSavesCount() {
        return savesCount;
    }

    public void setSavesCount(long savesCount) {
        this.savesCount = savesCount;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(long sharesCount) {
        this.sharesCount = sharesCount;
    }

    public long getRepostsCount() {
        return repostsCount;
    }

    public void setRepostsCount(long repostsCount) {
        this.repostsCount = repostsCount;
    }

    public ReactionType getCurrentUserReaction() {
        return currentUserReaction;
    }

    public void setCurrentUserReaction(ReactionType currentUserReaction) {
        this.currentUserReaction = currentUserReaction;
    }

    public boolean isSavedByCurrentUser() {
        return savedByCurrentUser;
    }

    public void setSavedByCurrentUser(boolean savedByCurrentUser) {
        this.savedByCurrentUser = savedByCurrentUser;
    }

    public boolean isRepostedByCurrentUser() {
        return repostedByCurrentUser;
    }

    public void setRepostedByCurrentUser(boolean repostedByCurrentUser) {
        this.repostedByCurrentUser = repostedByCurrentUser;
    }
}
