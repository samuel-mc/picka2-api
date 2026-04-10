package com.samuel_mc.pickados_api.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PresignCatalogLogoResponseDTO {
    private String uploadUrl;
    private String objectKey;
}
