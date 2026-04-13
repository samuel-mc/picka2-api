package com.samuel_mc.pickados_api.entity;

import com.samuel_mc.pickados_api.entity.enums.PostType;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
public class PostEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PostType type;

    @Column(nullable = false, length = 4000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PostVisibility visibility = PostVisibility.PUBLIC;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMediaEntity> media = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTagEntity> tags = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostPickEntity simplePick;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostParleyEntity parleyDetails;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParleySelectionEntity> parleySelections = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PostVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }

    public List<PostMediaEntity> getMedia() {
        return media;
    }

    public void setMedia(List<PostMediaEntity> media) {
        this.media = media;
    }

    public List<PostTagEntity> getTags() {
        return tags;
    }

    public void setTags(List<PostTagEntity> tags) {
        this.tags = tags;
    }

    public PostPickEntity getSimplePick() {
        return simplePick;
    }

    public void setSimplePick(PostPickEntity simplePick) {
        this.simplePick = simplePick;
    }

    public List<ParleySelectionEntity> getParleySelections() {
        return parleySelections;
    }

    public void setParleySelections(List<ParleySelectionEntity> parleySelections) {
        this.parleySelections = parleySelections;
    }

    public PostParleyEntity getParleyDetails() {
        return parleyDetails;
    }

    public void setParleyDetails(PostParleyEntity parleyDetails) {
        this.parleyDetails = parleyDetails;
    }
}
