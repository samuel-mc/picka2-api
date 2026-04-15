package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.dto.me.CompleteAvatarRequestDTO;
import com.samuel_mc.pickados_api.dto.me.MeProfileResponseDTO;
import com.samuel_mc.pickados_api.dto.me.UpdateMeProfileRequestDTO;
import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;
import com.samuel_mc.pickados_api.entity.CompetitionEntity;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.TeamEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.CompetitionRepository;
import com.samuel_mc.pickados_api.repository.TeamRepository;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class MeProfileService {

    private final UserRepository userRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final CompetitionRepository competitionRepository;
    private final TeamRepository teamRepository;
    private final ProfileAvatarStorageService profileAvatarStorageService;
    private final R2Properties r2Properties;

    public MeProfileService(UserRepository userRepository,
            TipsterProfileRepository tipsterProfileRepository,
            CompetitionRepository competitionRepository,
            TeamRepository teamRepository,
            ProfileAvatarStorageService profileAvatarStorageService,
            R2Properties r2Properties) {
        this.userRepository = userRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.competitionRepository = competitionRepository;
        this.teamRepository = teamRepository;
        this.profileAvatarStorageService = profileAvatarStorageService;
        this.r2Properties = r2Properties;
    }

    @Transactional(readOnly = true)
    public MeProfileResponseDTO getProfile(long userId) {
        UserEntity user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MeProfileResponseDTO dto = new MeProfileResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        if (isTipster(user)) {
            TipsterProfileEntity profile = tipsterProfileRepository.findByUser(user).orElse(null);
            dto.setBio(profile != null ? profile.getBio() : null);
            dto.setAvatarUrl(resolveAvatarUrl(profile != null ? profile.getAvatarUrl() : null));
        } else {
            dto.setBio(user.getBio());
            dto.setAvatarUrl(resolveAvatarUrl(user.getProfilePhotoKey()));
        }
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

    @Transactional
    public MeProfileResponseDTO updateProfile(long userId, UpdateMeProfileRequestDTO body) {
        UserEntity user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (body.getName() != null && !body.getName().isBlank()) {
            user.setName(body.getName().trim());
        }
        if (body.getLastname() != null && !body.getLastname().isBlank()) {
            user.setLastname(body.getLastname().trim());
        }
        if (body.getBio() != null) {
            String bioVal = body.getBio().isBlank() ? null : body.getBio().trim();
            if (isTipster(user)) {
                TipsterProfileEntity profile = tipsterProfileRepository.findByUser(user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de tipster no encontrado"));
                profile.setBio(bioVal);
                tipsterProfileRepository.save(profile);
            } else {
                user.setBio(bioVal);
            }
        }
        if (body.getPreferredCompetitionIds() != null || body.getPreferredTeamIds() != null) {
            syncPreferences(user, body.getPreferredCompetitionIds(), body.getPreferredTeamIds());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return getProfile(userId);
    }

    @Transactional
    public MeProfileResponseDTO completeAvatarUpload(long userId, CompleteAvatarRequestDTO body) {
        if (!profileAvatarStorageService.isKeyOwnedByUser(userId, body.getObjectKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clave de objeto no válida para este usuario");
        }
        UserEntity user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (isTipster(user)) {
            TipsterProfileEntity profile = tipsterProfileRepository.findByUser(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de tipster no encontrado"));
            profile.setAvatarUrl(body.getObjectKey());
            tipsterProfileRepository.save(profile);
        } else {
            user.setProfilePhotoKey(body.getObjectKey());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
        return getProfile(userId);
    }

    private boolean isTipster(UserEntity user) {
        return user.getRole() != null && "TIPSTER".equalsIgnoreCase(user.getRole().getName());
    }

    private void syncPreferences(UserEntity user, List<Long> preferredCompetitionIds, List<Long> preferredTeamIds) {
        Set<Long> competitionIds = toUniqueIds(preferredCompetitionIds);
        Set<Long> teamIds = toUniqueIds(preferredTeamIds);

        List<CompetitionEntity> competitions = competitionIds.isEmpty()
                ? List.of()
                : competitionRepository.findAllById(competitionIds);
        if (competitions.size() != competitionIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Una o más ligas seleccionadas no existen");
        }

        List<TeamEntity> teams = teamIds.isEmpty()
                ? List.of()
                : teamRepository.findAllById(teamIds);
        if (teams.size() != teamIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o más equipos seleccionados no existen");
        }

        // Si se eligen equipos, su competición también queda ligada al perfil.
        teams.stream()
                .map(team -> team.getCompetition().getId())
                .forEach(competitionIds::add);

        if (!competitionIds.isEmpty() && competitions.size() != competitionIds.size()) {
            competitions = competitionRepository.findAllById(competitionIds);
        }

        user.setPreferredCompetitions(new LinkedHashSet<>(competitions));
        user.setPreferredTeams(new LinkedHashSet<>(teams));
    }

    private Set<Long> toUniqueIds(List<Long> values) {
        if (values == null) {
            return new LinkedHashSet<>();
        }
        return values.stream()
                .filter(id -> id != null && id > 0)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
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
