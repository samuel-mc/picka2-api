package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.ResultStatus;

import java.time.LocalDateTime;

public class PostParleyResponseDTO {
    private Long id;
    private Integer stake;
    private SportsbookResponseDTO sportsbook;
    private LocalDateTime eventDate;
    private ResultStatus resultStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStake() {
        return stake;
    }

    public void setStake(Integer stake) {
        this.stake = stake;
    }

    public SportsbookResponseDTO getSportsbook() {
        return sportsbook;
    }

    public void setSportsbook(SportsbookResponseDTO sportsbook) {
        this.sportsbook = sportsbook;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }
}
