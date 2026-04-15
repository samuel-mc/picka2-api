package com.samuel_mc.pickados_api.repository.projection;

public interface AdminTipsterAnalyticsProjection {
    Long getUserId();
    String getDisplayName();
    String getUsername();
    Integer getValidatedTipster();
    Long getFollowersCount();
    Long getPostsCount();
    Long getTotalEngagement();
    Long getResolvedPicks();
    Long getWonPicks();
}
