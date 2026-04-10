package com.samuel_mc.pickados_api.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteCatalogLogoRequestDTO {

    @NotBlank(message = "La clave del logo es obligatoria")
    private String objectKey;
}
