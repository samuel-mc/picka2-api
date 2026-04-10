package com.samuel_mc.pickados_api.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompetitionRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String name;

    @NotNull(message = "El deporte es obligatorio")
    private Long sportId;

    @NotNull(message = "El país es obligatorio")
    private Long countryId;

    private Boolean active;
}
