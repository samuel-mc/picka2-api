package com.samuel_mc.pickados_api.dto.post;

public class FollowingFeedResponseDTO {
    private PagedResponseDTO<PostResponseDTO> feed;
    private long followingCount;

    public PagedResponseDTO<PostResponseDTO> getFeed() {
        return feed;
    }

    public void setFeed(PagedResponseDTO<PostResponseDTO> feed) {
        this.feed = feed;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }
}
