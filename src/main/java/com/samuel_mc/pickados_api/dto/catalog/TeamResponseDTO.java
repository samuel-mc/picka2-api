package com.samuel_mc.pickados_api.dto.catalog;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamResponseDTO {
    private Long id;
    private String name;
    private Boolean active;
    private String logoUrl;
    private Long competitionId;
    private String competitionName;
    private Long sportId;
    private String sportName;
    private Long countryId;
    private String countryName;
}
