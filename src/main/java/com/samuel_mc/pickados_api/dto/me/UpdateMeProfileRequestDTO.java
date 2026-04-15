package com.samuel_mc.pickados_api.dto.me;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateMeProfileRequestDTO {
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 160, message = "El apellido no puede exceder 160 caracteres")
    private String lastname;

    @Size(max = 2000, message = "La biografía no puede exceder 2000 caracteres")
    private String bio;

    private List<Long> preferredCompetitionIds;
    private List<Long> preferredTeamIds;
}
