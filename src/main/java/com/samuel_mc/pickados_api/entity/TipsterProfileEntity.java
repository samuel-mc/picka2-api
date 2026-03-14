package com.samuel_mc.pickados_api.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;
}
