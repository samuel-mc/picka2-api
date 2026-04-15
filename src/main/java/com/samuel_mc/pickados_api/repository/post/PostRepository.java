package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostEntity;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import com.samuel_mc.pickados_api.repository.projection.PostTimelineProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("""
            select p from PostEntity p
            where p.author.deleted = false and (
                p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.PUBLIC
                or p.author.id = :currentUserId
                or (
                    p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.FOLLOWERS_ONLY
                    and exists (
                        select 1 from FollowEntity f
                        where f.follower.id = :currentUserId and f.followed.id = p.author.id
                    )
                )
            )
            order by p.createdAt desc
            """)
    Page<PostEntity> findFeed(@Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query(value = """
            select t.postId as postId, t.eventAt as eventAt, t.repostId as repostId, t.repostUserId as repostUserId, t.repostCreatedAt as repostCreatedAt
            from (
                select
                    p.id as postId,
                    p.created_at as eventAt,
                    null as repostId,
                    null as repostUserId,
                    null as repostCreatedAt
                from posts p
                join users author on author.id = p.user_id
                where coalesce(author.deleted, false) = false
                  and (
                    p.visibility = 'PUBLIC'
                    or p.user_id = :currentUserId
                    or (
                        p.visibility = 'FOLLOWERS_ONLY'
                        and exists (
                            select 1 from follows f
                            where f.follower_id = :currentUserId and f.followed_id = p.user_id
                        )
                    )
                  )

                union all

                select
                    p.id as postId,
                    rp.created_at as eventAt,
                    rp.id as repostId,
                    rp.user_id as repostUserId,
                    rp.created_at as repostCreatedAt
                from post_reposts rp
                join posts p on p.id = rp.post_id
                join users author on author.id = p.user_id
                join users reposter on reposter.id = rp.user_id
                where coalesce(author.deleted, false) = false
                  and coalesce(reposter.deleted, false) = false
                  and p.visibility <> 'PRIVATE'
                  and (
                    rp.user_id = :currentUserId
                    or exists (
                        select 1 from follows f
                        where f.follower_id = :currentUserId and f.followed_id = rp.user_id
                    )
                  )
            ) t
            order by t.eventAt desc, t.postId desc
            """,
            countQuery = """
            select count(*)
            from (
                select p.id
                from posts p
                join users author on author.id = p.user_id
                where coalesce(author.deleted, false) = false
                  and (
                    p.visibility = 'PUBLIC'
                    or p.user_id = :currentUserId
                    or (
                        p.visibility = 'FOLLOWERS_ONLY'
                        and exists (
                            select 1 from follows f
                            where f.follower_id = :currentUserId and f.followed_id = p.user_id
                        )
                    )
                  )

                union all

                select rp.id
                from post_reposts rp
                join posts p on p.id = rp.post_id
                join users author on author.id = p.user_id
                join users reposter on reposter.id = rp.user_id
                where coalesce(author.deleted, false) = false
                  and coalesce(reposter.deleted, false) = false
                  and p.visibility <> 'PRIVATE'
                  and (
                    rp.user_id = :currentUserId
                    or exists (
                        select 1 from follows f
                        where f.follower_id = :currentUserId and f.followed_id = rp.user_id
                    )
                  )
            ) t
            """,
            nativeQuery = true)
    Page<PostTimelineProjection> findFeedTimeline(@Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query("""
            select p from PostEntity p
            where p.author.id = :authorId and p.author.deleted = false and (
                p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.PUBLIC
                or p.author.id = :currentUserId
                or (
                    p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.FOLLOWERS_ONLY
                    and exists (
                        select 1 from FollowEntity f
                        where f.follower.id = :currentUserId and f.followed.id = :authorId
                    )
                )
            )
            order by p.createdAt desc
            """)
    Page<PostEntity> findByAuthorVisibleToUser(@Param("authorId") Long authorId, @Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query(value = """
            select t.postId as postId, t.eventAt as eventAt, t.repostId as repostId, t.repostUserId as repostUserId, t.repostCreatedAt as repostCreatedAt
            from (
                select
                    p.id as postId,
                    p.created_at as eventAt,
                    null as repostId,
                    null as repostUserId,
                    null as repostCreatedAt
                from posts p
                join users author on author.id = p.user_id
                where p.user_id = :authorId
                  and coalesce(author.deleted, false) = false
                  and (
                    p.visibility = 'PUBLIC'
                    or p.user_id = :currentUserId
                    or (
                        p.visibility = 'FOLLOWERS_ONLY'
                        and exists (
                            select 1 from follows f
                            where f.follower_id = :currentUserId and f.followed_id = :authorId
                        )
                    )
                  )

                union all

                select
                    p.id as postId,
                    rp.created_at as eventAt,
                    rp.id as repostId,
                    rp.user_id as repostUserId,
                    rp.created_at as repostCreatedAt
                from post_reposts rp
                join posts p on p.id = rp.post_id
                join users reposter on reposter.id = rp.user_id
                join users author on author.id = p.user_id
                where rp.user_id = :authorId
                  and coalesce(reposter.deleted, false) = false
                  and coalesce(author.deleted, false) = false
                  and p.visibility <> 'PRIVATE'
                  and (
                    :authorId = :currentUserId
                    or exists (
                        select 1 from follows f
                        where f.follower_id = :currentUserId and f.followed_id = :authorId
                    )
                    or p.visibility = 'PUBLIC'
                    or p.user_id = :currentUserId
                    or (
                        p.visibility = 'FOLLOWERS_ONLY'
                        and exists (
                            select 1 from follows f2
                            where f2.follower_id = :currentUserId and f2.followed_id = p.user_id
                        )
                    )
                  )
            ) t
            order by t.eventAt desc, t.postId desc
            """,
            countQuery = """
            select count(*)
            from (
                select p.id
                from posts p
                join users author on author.id = p.user_id
                where p.user_id = :authorId
                  and coalesce(author.deleted, false) = false
                  and (
                    p.visibility = 'PUBLIC'
                    or p.user_id = :currentUserId
                    or (
                        p.visibility = 'FOLLOWERS_ONLY'
                        and exists (
                            select 1 from follows f
                            where f.follower_id = :currentUserId and f.followed_id = :authorId
                        )
                    )
                  )

                union all

                select rp.id
                from post_reposts rp
                join posts p on p.id = rp.post_id
                join users reposter on reposter.id = rp.user_id
                join users author on author.id = p.user_id
                where rp.user_id = :authorId
                  and coalesce(reposter.deleted, false) = false
                  and coalesce(author.deleted, false) = false
                  and p.visibility <> 'PRIVATE'
                  and (
                    :authorId = :currentUserId
                    or exists (
                        select 1 from follows f
                        where f.follower_id = :currentUserId and f.followed_id = :authorId
                    )
                    or p.visibility = 'PUBLIC'
                    or p.user_id = :currentUserId
                    or (
                        p.visibility = 'FOLLOWERS_ONLY'
                        and exists (
                            select 1 from follows f2
                            where f2.follower_id = :currentUserId and f2.followed_id = p.user_id
                        )
                    )
                  )
            ) t
            """,
            nativeQuery = true)
    Page<PostTimelineProjection> findTimelineByAuthorVisibleToUser(@Param("authorId") Long authorId, @Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query("""
            select p from SavedPostEntity s
            join s.post p
            where s.user.id = :currentUserId and p.author.deleted = false and (
                p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.PUBLIC
                or p.author.id = :currentUserId
                or (
                    p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.FOLLOWERS_ONLY
                    and exists (
                        select 1 from FollowEntity f
                        where f.follower.id = :currentUserId and f.followed.id = p.author.id
                    )
                )
            )
            order by s.createdAt desc
            """)
    Page<PostEntity> findSavedByUserVisibleToUser(@Param("currentUserId") Long currentUserId, Pageable pageable);

    @Query("""
            select p from PostEntity p
            where p.id = :postId and (
                p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.PUBLIC
                or p.author.id = :currentUserId
                or (
                    p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.FOLLOWERS_ONLY
                    and exists (
                        select 1 from FollowEntity f
                        where f.follower.id = :currentUserId and f.followed.id = p.author.id
                    )
                )
            )
            """)
    Optional<PostEntity> findVisibleById(@Param("postId") Long postId, @Param("currentUserId") Long currentUserId);

    @Query("""
            select p from PostEntity p
            where p.id = :postId
              and p.visibility = com.samuel_mc.pickados_api.entity.enums.PostVisibility.PUBLIC
              and p.author.deleted = false
            """)
    Optional<PostEntity> findPublicById(@Param("postId") Long postId);

    long countByAuthorIdAndVisibility(Long authorId, PostVisibility visibility);

    long countByAuthorDeletedFalse();

    long countByCreatedAtAfter(LocalDateTime createdAt);

    long countByType(com.samuel_mc.pickados_api.entity.enums.PostType type);

    long countByVisibility(PostVisibility visibility);
}
