package com.samuel_mc.pickados_api.service.post;

import com.samuel_mc.pickados_api.dto.post.SportsbookResponseDTO;
import com.samuel_mc.pickados_api.repository.post.SportsbookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SportsbookService {

    private final SportsbookRepository sportsbookRepository;

    public SportsbookService(SportsbookRepository sportsbookRepository) {
        this.sportsbookRepository = sportsbookRepository;
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
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
