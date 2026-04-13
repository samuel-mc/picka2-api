package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;
import com.samuel_mc.pickados_api.dto.user.PublicProfileResponseDTO;
import com.samuel_mc.pickados_api.entity.CompetitionEntity;
import com.samuel_mc.pickados_api.entity.TeamEntity;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.repository.post.FollowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final FollowRepository followRepository;
    private final R2Properties r2Properties;

    public UserProfileService(
            UserRepository userRepository,
            TipsterProfileRepository tipsterProfileRepository,
            FollowRepository followRepository,
            R2Properties r2Properties
    ) {
        this.userRepository = userRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.followRepository = followRepository;
        this.r2Properties = r2Properties;
    }

    @Transactional(readOnly = true)
    public PublicProfileResponseDTO getPublicProfile(long currentUserId, long targetUserId) {
        UserEntity user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        TipsterProfileEntity tipsterProfile = tipsterProfileRepository.findByUser(user).orElse(null);

        PublicProfileResponseDTO dto = new PublicProfileResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setUsername(user.getUsername());
        dto.setBio(tipsterProfile != null ? tipsterProfile.getBio() : user.getBio());
        dto.setAvatarUrl(resolveAvatarUrl(tipsterProfile != null ? tipsterProfile.getAvatarUrl() : user.getProfilePhotoKey()));
        dto.setValidatedTipster(tipsterProfile != null && Boolean.TRUE.equals(tipsterProfile.getValidated()));
        dto.setSelfProfile(currentUserId == targetUserId);
        dto.setFollowedByCurrentUser(currentUserId != targetUserId
                && followRepository.findByFollowerIdAndFollowedId(currentUserId, targetUserId).isPresent());
        dto.setFollowersCount(followRepository.countByFollowedId(targetUserId));
        dto.setFollowingCount(followRepository.countByFollowerId(targetUserId));
        dto.setPreferredCompetitions(user.getPreferredCompetitions().stream()
                .sorted(Comparator.comparing(CompetitionEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::mapCompetition)
                .toList());
        dto.setPreferredTeams(user.getPreferredTeams().stream()
                .sorted(Comparator.comparing(TeamEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::mapTeam)
                .toList());
        return dto;
    }

    private CompetitionResponseDTO mapCompetition(CompetitionEntity entity) {
        return CompetitionResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.getActive())
                .logoUrl(resolveAvatarUrl(entity.getLogoKey()))
                .sportId(entity.getSport().getId())
                .sportName(entity.getSport().getName())
                .countryId(entity.getCountry().getId())
                .countryName(entity.getCountry().getName())
                .build();
    }

    private TeamResponseDTO mapTeam(TeamEntity entity) {
        CompetitionEntity competition = entity.getCompetition();
        return TeamResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.getActive())
                .logoUrl(resolveAvatarUrl(entity.getLogoKey()))
                .competitionId(competition.getId())
                .competitionName(competition.getName())
                .sportId(competition.getSport().getId())
                .sportName(competition.getSport().getName())
                .countryId(competition.getCountry().getId())
                .countryName(competition.getCountry().getName())
                .build();
    }

    private String resolveAvatarUrl(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        String s = stored.trim();
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return s;
        }
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            return null;
        }
        return base.replaceAll("/$", "") + "/" + s;
    }
}
