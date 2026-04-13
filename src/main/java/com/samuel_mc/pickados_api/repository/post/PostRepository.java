package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.PostEntity;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    long countByAuthorIdAndVisibility(Long authorId, PostVisibility visibility);
}
