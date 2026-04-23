package com.samuel_mc.pickados_api.service.post;

import com.samuel_mc.pickados_api.dto.post.SportsbookResponseDTO;
import com.samuel_mc.pickados_api.repository.post.SportsbookRepository;
import com.samuel_mc.pickados_api.service.CatalogLogoStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SportsbookService {

    private final SportsbookRepository sportsbookRepository;
    private final CatalogLogoStorageService catalogLogoStorageService;

    public SportsbookService(SportsbookRepository sportsbookRepository, CatalogLogoStorageService catalogLogoStorageService) {
        this.sportsbookRepository = sportsbookRepository;
        this.catalogLogoStorageService = catalogLogoStorageService;
    }

    @Transactional(readOnly = true)
    public List<SportsbookResponseDTO> getAll() {
        return sportsbookRepository.findAllByOrderByNameAsc().stream()
                .map(this::map)
                .toList();
    }

    public SportsbookResponseDTO map(com.samuel_mc.pickados_api.entity.SportsbookEntity entity) {
        SportsbookResponseDTO dto = new SportsbookResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBaseUrl(entity.getBaseUrl());
        dto.setLogoUrl(catalogLogoStorageService.resolvePublicUrl(entity.getLogoKey()));
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
