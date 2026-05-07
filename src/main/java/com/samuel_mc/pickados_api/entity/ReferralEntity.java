package com.samuel_mc.pickados_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "referrals", indexes = {
        @Index(name = "idx_referrals_inviter", columnList = "inviter_user_id"),
        @Index(name = "idx_referrals_invited", columnList = "invited_user_id", unique = true)
})
public class ReferralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_user_id", nullable = false)
    private UserEntity inviter;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id", nullable = false, unique = true)
    private UserEntity invited;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

