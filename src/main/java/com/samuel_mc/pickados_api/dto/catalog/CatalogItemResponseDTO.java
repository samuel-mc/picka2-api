package com.samuel_mc.pickados_api.dto.catalog;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CatalogItemResponseDTO {
    private Long id;
    private String name;
    private Boolean active;
    private String logoUrl;
}
