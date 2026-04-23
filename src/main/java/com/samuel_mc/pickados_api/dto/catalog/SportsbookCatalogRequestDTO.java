package com.samuel_mc.pickados_api.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SportsbookCatalogRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre es demasiado largo")
    private String name;

    @Size(max = 255, message = "La URL base es demasiado larga")
    private String baseUrl;

    private Boolean active;
}

