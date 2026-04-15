package com.samuel_mc.pickados_api.dto.analytics;

import java.time.LocalDateTime;
import java.util.List;

public record AdminAnalyticsResponseDTO(
        LocalDateTime generatedAt,
        Totals totals,
        Last30Days last30Days,
        Rates rates,
        List<BreakdownItem> postTypeBreakdown,
        List<BreakdownItem> visibilityBreakdown,
        List<BreakdownItem> pickResultBreakdown,
        List<BreakdownItem> preferredCompetitions,
        List<BreakdownItem> preferredTeams,
        List<TopTipster> topTipsters
) {
    public record Totals(
            long totalAccounts,
            long totalAdmins,
            long totalTipsters,
            long validatedTipsters,
            long totalPosts,
            long totalComments,
            long totalReactions,
            long totalSaves,
            long totalUniqueViews,
            long totalShares,
            long totalReposts,
            long totalFollows,
            long resolvedPicks
    ) {}

    public record Last30Days(
            long newTipsters,
            long newPosts,
            long newComments,
            long newReactions,
            long newFollows,
            long newShares,
            long recentlyResolvedPicks
    ) {}

    public record Rates(
            double engagementPerPost,
            double interactionRateOverViews,
            double saveRateOverViews,
            double shareRateOverViews,
            double commentRateOverViews,
            double likeRateOverViews,
            double winRate
    ) {}

    public record BreakdownItem(
            String label,
            long value
    ) {}

    public record TopTipster(
            Long userId,
            String displayName,
            String username,
            boolean validatedTipster,
            long followersCount,
            long postsCount,
            long totalEngagement,
            long resolvedPicks,
            long wonPicks,
            double winRate
    ) {}
}
