package com.samuel_mc.pickados_api.dto.post;

import jakarta.validation.constraints.NotNull;

public class ParleySelectionRequestDTO {

    @NotNull(message = "El deporte de la selección es obligatorio")
    private Long sportId;

    @NotNull(message = "La liga de la selección es obligatoria")
    private Long leagueId;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }
}
