package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.projection.AdminTipsterAnalyticsProjection;
import com.samuel_mc.pickados_api.repository.projection.NamedAggregateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.username = :input OR u.email = :input")
    Optional<UserEntity> findByUsernameOrEmail(@Param("input") String input);

    @Query("""
            SELECT u
            FROM UserEntity u
            WHERE (u.username = :input OR u.email = :input)
              AND coalesce(u.deleted, false) = false
            """)
    Optional<UserEntity> findActiveByUsernameOrEmail(@Param("input") String input);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    Optional<UserEntity> findByIdAndDeletedFalse(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserEntity> findByRole_NameAndDeletedFalse(String roleName);

    List<UserEntity> findByDeletedFalseOrderByCreatedAtDesc();

    List<UserEntity> findByRole_NameAndDeletedFalseOrderByCreatedAtDesc(String roleName);

    List<UserEntity> findByRole_NameNotAndDeletedFalseOrderByCreatedAtDesc(String roleName);

    long countByDeletedFalse();

    long countByRole_NameAndDeletedFalse(String roleName);

    long countByRole_NameAndDeletedFalseAndCreatedAtAfter(String roleName, LocalDateTime createdAt);

    @Query("""
            select c.name as label, count(u.id) as total
            from UserEntity u join u.preferredCompetitions c
            where u.deleted = false
            group by c.id, c.name
            order by count(u.id) desc
            """)
    List<NamedAggregateProjection> countUsersByPreferredCompetition();

    @Query("""
            select t.name as label, count(u.id) as total
            from UserEntity u join u.preferredTeams t
            where u.deleted = false
            group by t.id, t.name
            order by count(u.id) desc
            """)
    List<NamedAggregateProjection> countUsersByPreferredTeam();

    @Query(value = """
            select
              u.id as "userId",
              trim(concat(coalesce(u.name, ''), ' ', coalesce(u.lastname, ''))) as "displayName",
              u.username as "username",
              case when coalesce(tp.validated, false) then 1 else 0 end as "validatedTipster",
              (select count(*) from follows f where f.followed_id = u.id) as "followersCount",
              (select count(*) from posts p where p.user_id = u.id) as "postsCount",
              (
                (select count(*) from comments c join posts p1 on p1.id = c.post_id where p1.user_id = u.id)
                + (select count(*) from reactions r1 join posts p2 on p2.id = r1.post_id where p2.user_id = u.id)
                + (select count(*) from saved_posts s join posts p3 on p3.id = s.post_id where p3.user_id = u.id)
                + (select count(*) from post_shares sh join posts p4 on p4.id = sh.post_id where p4.user_id = u.id)
                + (select count(*) from post_reposts rp join posts p5 on p5.id = rp.post_id where p5.user_id = u.id)
              ) as "totalEngagement",
              (
                (select count(*) from post_picks pk join posts p6 on p6.id = pk.post_id where p6.user_id = u.id and pk.result_status <> 'PENDING')
                + (select count(*) from post_parleys py join posts p7 on p7.id = py.post_id where p7.user_id = u.id and py.result_status <> 'PENDING')
              ) as "resolvedPicks",
              (
                (select count(*) from post_picks pk join posts p8 on p8.id = pk.post_id where p8.user_id = u.id and pk.result_status = 'WON')
                + (select count(*) from post_parleys py join posts p9 on p9.id = py.post_id where p9.user_id = u.id and py.result_status = 'WON')
              ) as "wonPicks"
            from users u
            join roles r on r.id = u.role_id
            left join tipsters_profile tp on tp.user_id = u.id
            where r.name = 'TIPSTER' and coalesce(u.deleted, false) = false
            order by totalEngagement desc, followersCount desc, postsCount desc, u.id desc
            limit 5
            """, nativeQuery = true)
    List<AdminTipsterAnalyticsProjection> findTopTipstersForAnalytics();
}
