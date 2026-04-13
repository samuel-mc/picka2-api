package com.samuel_mc.pickados_api.dto.me;

import lombok.Data;

import java.util.List;

@Data
public class UpdateMeProfileRequestDTO {
    private String name;
    private String lastname;
    private String bio;
    private List<Long> preferredCompetitionIds;
    private List<Long> preferredTeamIds;
}
