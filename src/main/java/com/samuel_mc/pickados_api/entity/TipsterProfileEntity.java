package com.samuel_mc.pickados_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "tipsters_profile")
public class TipsterProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "validated", nullable = false, columnDefinition = "boolean default false")
    private Boolean validated;

    @Column(name = "referral_badge", nullable = false, columnDefinition = "boolean default false")
    private Boolean referralBadge = false;

    @Column(name = "boost_until")
    private java.time.LocalDateTime boostUntil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;
}
