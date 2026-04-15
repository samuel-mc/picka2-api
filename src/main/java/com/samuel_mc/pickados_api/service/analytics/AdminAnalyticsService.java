package com.samuel_mc.pickados_api.service.analytics;

import com.samuel_mc.pickados_api.dto.analytics.AdminAnalyticsResponseDTO;
import com.samuel_mc.pickados_api.entity.enums.PostType;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import com.samuel_mc.pickados_api.entity.enums.ReactionType;
import com.samuel_mc.pickados_api.entity.enums.ResultStatus;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.repository.post.CommentRepository;
import com.samuel_mc.pickados_api.repository.post.FollowRepository;
import com.samuel_mc.pickados_api.repository.post.PostParleyRepository;
import com.samuel_mc.pickados_api.repository.post.PostPickRepository;
import com.samuel_mc.pickados_api.repository.post.PostRepository;
import com.samuel_mc.pickados_api.repository.post.PostRepostRepository;
import com.samuel_mc.pickados_api.repository.post.PostShareRepository;
import com.samuel_mc.pickados_api.repository.post.PostViewRepository;
import com.samuel_mc.pickados_api.repository.post.ReactionRepository;
import com.samuel_mc.pickados_api.repository.post.SavedPostRepository;
import com.samuel_mc.pickados_api.repository.projection.AdminTipsterAnalyticsProjection;
import com.samuel_mc.pickados_api.repository.projection.NamedAggregateProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminAnalyticsService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_TIPSTER = "TIPSTER";

    private final UserRepository userRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final SavedPostRepository savedPostRepository;
    private final PostViewRepository postViewRepository;
    private final PostShareRepository postShareRepository;
    private final PostRepostRepository postRepostRepository;
    private final FollowRepository followRepository;
    private final PostPickRepository postPickRepository;
    private final PostParleyRepository postParleyRepository;

    public AdminAnalyticsService(
            UserRepository userRepository,
            TipsterProfileRepository tipsterProfileRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            ReactionRepository reactionRepository,
            SavedPostRepository savedPostRepository,
            PostViewRepository postViewRepository,
            PostShareRepository postShareRepository,
            PostRepostRepository postRepostRepository,
            FollowRepository followRepository,
            PostPickRepository postPickRepository,
            PostParleyRepository postParleyRepository
    ) {
        this.userRepository = userRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
        this.savedPostRepository = savedPostRepository;
        this.postViewRepository = postViewRepository;
        this.postShareRepository = postShareRepository;
        this.postRepostRepository = postRepostRepository;
        this.followRepository = followRepository;
        this.postPickRepository = postPickRepository;
        this.postParleyRepository = postParleyRepository;
    }

    @Transactional(readOnly = true)
    public AdminAnalyticsResponseDTO getOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last30Days = now.minusDays(30);

        long totalAdmins = userRepository.countByRole_NameAndDeletedFalse(ROLE_ADMIN);
        long totalTipsters = userRepository.countByRole_NameAndDeletedFalse(ROLE_TIPSTER);
        long totalAccounts = totalAdmins + totalTipsters;
        long validatedTipsters = tipsterProfileRepository.countByValidatedTrue();
        long totalPosts = postRepository.countByAuthorDeletedFalse();
        long totalComments = commentRepository.count();
        long totalReactions = reactionRepository.count();
        long totalSaves = savedPostRepository.count();
        long totalUniqueViews = postViewRepository.count();
        long totalShares = postShareRepository.count();
        long totalReposts = postRepostRepository.count();
        long totalFollows = followRepository.count();
        long resolvedPicks = countResolvedPicks();

        long likes = reactionRepository.countByType(ReactionType.LIKE);
        long interactions = totalComments + totalReactions + totalSaves + totalShares + totalReposts;

        return new AdminAnalyticsResponseDTO(
                now,
                new AdminAnalyticsResponseDTO.Totals(
                        totalAccounts,
                        totalAdmins,
                        totalTipsters,
                        validatedTipsters,
                        totalPosts,
                        totalComments,
                        totalReactions,
                        totalSaves,
                        totalUniqueViews,
                        totalShares,
                        totalReposts,
                        totalFollows,
                        resolvedPicks
                ),
                new AdminAnalyticsResponseDTO.Last30Days(
                        userRepository.countByRole_NameAndDeletedFalseAndCreatedAtAfter(ROLE_TIPSTER, last30Days),
                        postRepository.countByCreatedAtAfter(last30Days),
                        commentRepository.countByCreatedAtAfter(last30Days),
                        reactionRepository.countByCreatedAtAfter(last30Days),
                        followRepository.countByCreatedAtAfter(last30Days),
                        postShareRepository.countByCreatedAtAfter(last30Days),
                        countRecentlyResolvedPicks(last30Days)
                ),
                new AdminAnalyticsResponseDTO.Rates(
                        ratio(interactions, totalPosts),
                        ratio(interactions, totalUniqueViews),
                        ratio(totalSaves, totalUniqueViews),
                        ratio(totalShares, totalUniqueViews),
                        ratio(totalComments, totalUniqueViews),
                        ratio(likes, totalUniqueViews),
                        winRate()
                ),
                mapPostTypeBreakdown(),
                mapVisibilityBreakdown(),
                mapPickResultBreakdown(),
                mapBreakdown(userRepository.countUsersByPreferredCompetition()),
                mapBreakdown(userRepository.countUsersByPreferredTeam()),
                mapTopTipsters(userRepository.findTopTipstersForAnalytics())
        );
    }

    private List<AdminAnalyticsResponseDTO.BreakdownItem> mapPostTypeBreakdown() {
        return Arrays.stream(PostType.values())
                .map(type -> new AdminAnalyticsResponseDTO.BreakdownItem(type.name(), postRepository.countByType(type)))
                .toList();
    }

    private List<AdminAnalyticsResponseDTO.BreakdownItem> mapVisibilityBreakdown() {
        return Arrays.stream(PostVisibility.values())
                .map(visibility -> new AdminAnalyticsResponseDTO.BreakdownItem(
                        visibility.name(),
                        postRepository.countByVisibility(visibility)
                ))
                .toList();
    }

    private List<AdminAnalyticsResponseDTO.BreakdownItem> mapPickResultBreakdown() {
        return Arrays.stream(ResultStatus.values())
                .map(status -> new AdminAnalyticsResponseDTO.BreakdownItem(status.name(), countPickResult(status)))
                .toList();
    }

    private List<AdminAnalyticsResponseDTO.BreakdownItem> mapBreakdown(List<NamedAggregateProjection> rows) {
        return rows.stream()
                .limit(10)
                .map(row -> new AdminAnalyticsResponseDTO.BreakdownItem(row.getLabel(), row.getTotal()))
                .toList();
    }

    private List<AdminAnalyticsResponseDTO.TopTipster> mapTopTipsters(List<AdminTipsterAnalyticsProjection> rows) {
        return rows.stream()
                .map(row -> new AdminAnalyticsResponseDTO.TopTipster(
                        row.getUserId(),
                        row.getDisplayName(),
                        row.getUsername(),
                        row.getValidatedTipster() != null && row.getValidatedTipster() > 0,
                        safeLong(row.getFollowersCount()),
                        safeLong(row.getPostsCount()),
                        safeLong(row.getTotalEngagement()),
                        safeLong(row.getResolvedPicks()),
                        safeLong(row.getWonPicks()),
                        ratio(safeLong(row.getWonPicks()), safeLong(row.getResolvedPicks()))
                ))
                .toList();
    }

    private long countPickResult(ResultStatus status) {
        return postPickRepository.countByResultStatus(status) + postParleyRepository.countByResultStatus(status);
    }

    private long countResolvedPicks() {
        return postPickRepository.countByResultStatusNot(ResultStatus.PENDING)
                + postParleyRepository.countByResultStatusNot(ResultStatus.PENDING);
    }

    private long countRecentlyResolvedPicks(LocalDateTime last30Days) {
        return postPickRepository.countByResultStatusNotAndUpdatedAtAfter(ResultStatus.PENDING, last30Days)
                + postParleyRepository.countByResultStatusNotAndUpdatedAtAfter(ResultStatus.PENDING, last30Days);
    }

    private double winRate() {
        long won = countPickResult(ResultStatus.WON);
        long lost = countPickResult(ResultStatus.LOST);
        return ratio(won, won + lost);
    }

    private double ratio(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0;
        }
        return (double) numerator / (double) denominator;
    }

    private long safeLong(Long value) {
        return value == null ? 0 : value;
    }
}
