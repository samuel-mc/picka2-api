package com.samuel_mc.pickados_api.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteCatalogLogoRequestDTO {

    @NotBlank(message = "La clave del logo es obligatoria")
    @Size(max = 512, message = "La clave del logo es demasiado larga")
    private String objectKey;
}
