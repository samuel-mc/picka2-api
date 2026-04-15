package com.samuel_mc.pickados_api.dto.notification;

public class NotificationActorResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String avatarUrl;
    private Boolean validatedTipster;
    private String badge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getValidatedTipster() {
        return validatedTipster;
    }

    public void setValidatedTipster(Boolean validatedTipster) {
        this.validatedTipster = validatedTipster;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}
