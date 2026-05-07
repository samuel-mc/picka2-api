package com.samuel_mc.pickados_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "referral_codes", indexes = {
        @Index(name = "idx_referral_codes_code", columnList = "code", unique = true)
})
public class ReferralCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(nullable = false, unique = true, length = 24)
    private String code;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

