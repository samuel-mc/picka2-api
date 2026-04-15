package com.samuel_mc.pickados_api.dto.me;

import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class MeProfileResponseDTO {
    private long id;
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
    private List<CompetitionResponseDTO> preferredCompetitions;
    private List<TeamResponseDTO> preferredTeams;
}
