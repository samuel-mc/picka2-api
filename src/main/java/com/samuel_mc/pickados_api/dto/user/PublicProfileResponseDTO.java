package com.samuel_mc.pickados_api.dto.user;

import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;

import java.util.List;

public class PublicProfileResponseDTO {
    private Long id;
    private String name;
    private String lastname;
    private String username;
    private String bio;
    private String avatarUrl;
    private Boolean validatedTipster;
    private Boolean followedByCurrentUser;
    private Boolean selfProfile;
    private Long followersCount;
    private Long followingCount;
    private List<CompetitionResponseDTO> preferredCompetitions;
    private List<TeamResponseDTO> preferredTeams;

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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public Boolean getFollowedByCurrentUser() {
        return followedByCurrentUser;
    }

    public void setFollowedByCurrentUser(Boolean followedByCurrentUser) {
        this.followedByCurrentUser = followedByCurrentUser;
    }

    public Boolean getSelfProfile() {
        return selfProfile;
    }

    public void setSelfProfile(Boolean selfProfile) {
        this.selfProfile = selfProfile;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    public List<CompetitionResponseDTO> getPreferredCompetitions() {
        return preferredCompetitions;
    }

    public void setPreferredCompetitions(List<CompetitionResponseDTO> preferredCompetitions) {
        this.preferredCompetitions = preferredCompetitions;
    }

    public List<TeamResponseDTO> getPreferredTeams() {
        return preferredTeams;
    }

    public void setPreferredTeams(List<TeamResponseDTO> preferredTeams) {
        this.preferredTeams = preferredTeams;
    }
}
