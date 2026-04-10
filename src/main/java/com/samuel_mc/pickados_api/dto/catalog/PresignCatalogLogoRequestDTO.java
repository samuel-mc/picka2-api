package com.samuel_mc.pickados_api.dto.catalog;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresignCatalogLogoRequestDTO {

    @NotBlank(message = "El tipo de contenido es obligatorio")
    private String contentType;
}
