package com.samuel_mc.pickados_api.dto.catalog;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SportsbookCatalogResponseDTO {
    private Long id;
    private String name;
    private Boolean active;
    private String baseUrl;
    private String logoUrl;
}

