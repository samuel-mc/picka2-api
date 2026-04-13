package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.dto.catalog.CatalogItemRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CatalogItemResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompleteCatalogLogoRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.PresignCatalogLogoResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;
import com.samuel_mc.pickados_api.entity.CompetitionEntity;
import com.samuel_mc.pickados_api.entity.CountryEntity;
import com.samuel_mc.pickados_api.entity.HomePrasheEntity;
import com.samuel_mc.pickados_api.entity.SportEntity;
import com.samuel_mc.pickados_api.entity.TeamEntity;
import com.samuel_mc.pickados_api.exception.GenericException;
import com.samuel_mc.pickados_api.repository.CompetitionRepository;
import com.samuel_mc.pickados_api.repository.CountryRepository;
import com.samuel_mc.pickados_api.repository.HomePrasheRepository;
import com.samuel_mc.pickados_api.repository.SportRepository;
import com.samuel_mc.pickados_api.repository.TeamRepository;
import com.samuel_mc.pickados_api.service.CatalogLogoStorageService;
import com.samuel_mc.pickados_api.service.CatalogService;
import com.samuel_mc.pickados_api.util.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class CatalogServiceImpl implements CatalogService {

    private final SportRepository sportRepository;
    private final CountryRepository countryRepository;
    private final CompetitionRepository competitionRepository;
    private final TeamRepository teamRepository;
    private final HomePrasheRepository homePrasheRepository;
    private final CatalogLogoStorageService catalogLogoStorageService;

    public CatalogServiceImpl(
            SportRepository sportRepository,
            CountryRepository countryRepository,
            CompetitionRepository competitionRepository,
            TeamRepository teamRepository,
            HomePrasheRepository homePrasheRepository,
            CatalogLogoStorageService catalogLogoStorageService
    ) {
        this.sportRepository = sportRepository;
        this.countryRepository = countryRepository;
        this.competitionRepository = competitionRepository;
        this.teamRepository = teamRepository;
        this.homePrasheRepository = homePrasheRepository;
        this.catalogLogoStorageService = catalogLogoStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogItemResponseDTO> getSports() {
        return sportRepository.findAll().stream()
                .map(this::mapSport)
                .toList();
    }

    @Override
    public CatalogItemResponseDTO createSport(CatalogItemRequestDTO request) {
        String normalizedName = normalizeName(request.getName());
        sportRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Ya existe un deporte con ese nombre");
                });

        SportEntity entity = new SportEntity();
        entity.setName(normalizedName);
        entity.setActive(resolveActive(request.getActive()));
        return mapSport(sportRepository.save(entity));
    }

    @Override
    public CatalogItemResponseDTO updateSport(Long id, CatalogItemRequestDTO request) {
        SportEntity entity = getSport(id);
        String normalizedName = normalizeName(request.getName());
        sportRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Ya existe un deporte con ese nombre");
                });

        entity.setName(normalizedName);
        entity.setActive(resolveActive(request.getActive()));
        return mapSport(sportRepository.save(entity));
    }

    @Override
    public PresignCatalogLogoResponseDTO presignSportLogo(Long id, String contentType) {
        getSport(id);
        return catalogLogoStorageService.presignPut("sports", id, contentType);
    }

    @Override
    public CatalogItemResponseDTO completeSportLogo(Long id, CompleteCatalogLogoRequestDTO request) {
        SportEntity entity = getSport(id);
        if (!catalogLogoStorageService.isOwnedByCatalogEntity("sports", id, request.getObjectKey())) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Clave de logo no válida para este deporte");
        }
        entity.setLogoKey(request.getObjectKey());
        return mapSport(sportRepository.save(entity));
    }

    @Override
    public void deleteSport(Long id) {
        SportEntity entity = getSport(id);
        if (competitionRepository.countBySport_Id(id) > 0) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                    "No se puede eliminar el deporte porque tiene competiciones asociadas");
        }
        sportRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogItemResponseDTO> getCountries() {
        return countryRepository.findAll().stream()
                .map(this::mapCountry)
                .toList();
    }

    @Override
    public CatalogItemResponseDTO createCountry(CatalogItemRequestDTO request) {
        String normalizedName = normalizeName(request.getName());
        countryRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Ya existe un país con ese nombre");
                });

        CountryEntity entity = new CountryEntity();
        entity.setName(normalizedName);
        entity.setActive(resolveActive(request.getActive()));
        return mapCountry(countryRepository.save(entity));
    }

    @Override
    public CatalogItemResponseDTO updateCountry(Long id, CatalogItemRequestDTO request) {
        CountryEntity entity = getCountry(id);
        String normalizedName = normalizeName(request.getName());
        countryRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Ya existe un país con ese nombre");
                });

        entity.setName(normalizedName);
        entity.setActive(resolveActive(request.getActive()));
        return mapCountry(countryRepository.save(entity));
    }

    @Override
    public PresignCatalogLogoResponseDTO presignCountryLogo(Long id, String contentType) {
        getCountry(id);
        return catalogLogoStorageService.presignPut("countries", id, contentType);
    }

    @Override
    public CatalogItemResponseDTO completeCountryLogo(Long id, CompleteCatalogLogoRequestDTO request) {
        CountryEntity entity = getCountry(id);
        if (!catalogLogoStorageService.isOwnedByCatalogEntity("countries", id, request.getObjectKey())) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Clave de logo no válida para este país");
        }
        entity.setLogoKey(request.getObjectKey());
        return mapCountry(countryRepository.save(entity));
    }

    @Override
    public void deleteCountry(Long id) {
        CountryEntity entity = getCountry(id);
        if (competitionRepository.countByCountry_Id(id) > 0) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                    "No se puede eliminar el país porque tiene competiciones asociadas");
        }
        countryRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitionResponseDTO> getCompetitions() {
        return competitionRepository.findAll().stream()
                .map(this::mapCompetition)
                .toList();
    }

    @Override
    public CompetitionResponseDTO createCompetition(CompetitionRequestDTO request) {
        String normalizedName = normalizeName(request.getName());
        SportEntity sport = getSport(request.getSportId());
        CountryEntity country = getCountry(request.getCountryId());

        competitionRepository.findByNameIgnoreCaseAndSport_IdAndCountry_Id(normalizedName, sport.getId(), country.getId())
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                            "Ya existe una competición con ese nombre para el mismo deporte y país");
                });

        CompetitionEntity entity = new CompetitionEntity();
        entity.setName(normalizedName);
        entity.setSport(sport);
        entity.setCountry(country);
        entity.setActive(resolveActive(request.getActive()));
        return mapCompetition(competitionRepository.save(entity));
    }

    @Override
    public CompetitionResponseDTO updateCompetition(Long id, CompetitionRequestDTO request) {
        CompetitionEntity entity = getCompetition(id);
        String normalizedName = normalizeName(request.getName());
        SportEntity sport = getSport(request.getSportId());
        CountryEntity country = getCountry(request.getCountryId());

        competitionRepository.findByNameIgnoreCaseAndSport_IdAndCountry_Id(normalizedName, sport.getId(), country.getId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                            "Ya existe una competición con ese nombre para el mismo deporte y país");
                });

        entity.setName(normalizedName);
        entity.setSport(sport);
        entity.setCountry(country);
        entity.setActive(resolveActive(request.getActive()));
        return mapCompetition(competitionRepository.save(entity));
    }

    @Override
    public PresignCatalogLogoResponseDTO presignCompetitionLogo(Long id, String contentType) {
        getCompetition(id);
        return catalogLogoStorageService.presignPut("competitions", id, contentType);
    }

    @Override
    public CompetitionResponseDTO completeCompetitionLogo(Long id, CompleteCatalogLogoRequestDTO request) {
        CompetitionEntity entity = getCompetition(id);
        if (!catalogLogoStorageService.isOwnedByCatalogEntity("competitions", id, request.getObjectKey())) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Clave de logo no válida para esta competición");
        }
        entity.setLogoKey(request.getObjectKey());
        return mapCompetition(competitionRepository.save(entity));
    }

    @Override
    public void deleteCompetition(Long id) {
        CompetitionEntity entity = getCompetition(id);
        if (teamRepository.countByCompetition_Id(id) > 0) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                    "No se puede eliminar la competición porque tiene equipos asociados");
        }
        competitionRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getTeams() {
        return teamRepository.findAll().stream()
                .map(this::mapTeam)
                .toList();
    }

    @Override
    public TeamResponseDTO createTeam(TeamRequestDTO request) {
        String normalizedName = normalizeName(request.getName());
        CompetitionEntity competition = getCompetition(request.getCompetitionId());

        teamRepository.findByNameIgnoreCaseAndCompetition_Id(normalizedName, competition.getId())
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                            "Ya existe un equipo con ese nombre en la competición seleccionada");
                });

        TeamEntity entity = new TeamEntity();
        entity.setName(normalizedName);
        entity.setCompetition(competition);
        entity.setActive(resolveActive(request.getActive()));
        return mapTeam(teamRepository.save(entity));
    }

    @Override
    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO request) {
        TeamEntity entity = getTeam(id);
        String normalizedName = normalizeName(request.getName());
        CompetitionEntity competition = getCompetition(request.getCompetitionId());

        teamRepository.findByNameIgnoreCaseAndCompetition_Id(normalizedName, competition.getId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(),
                            "Ya existe un equipo con ese nombre en la competición seleccionada");
                });

        entity.setName(normalizedName);
        entity.setCompetition(competition);
        entity.setActive(resolveActive(request.getActive()));
        return mapTeam(teamRepository.save(entity));
    }

    @Override
    public PresignCatalogLogoResponseDTO presignTeamLogo(Long id, String contentType) {
        getTeam(id);
        return catalogLogoStorageService.presignPut("teams", id, contentType);
    }

    @Override
    public TeamResponseDTO completeTeamLogo(Long id, CompleteCatalogLogoRequestDTO request) {
        TeamEntity entity = getTeam(id);
        if (!catalogLogoStorageService.isOwnedByCatalogEntity("teams", id, request.getObjectKey())) {
            throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Clave de logo no válida para este equipo");
        }
        entity.setLogoKey(request.getObjectKey());
        return mapTeam(teamRepository.save(entity));
    }

    @Override
    public void deleteTeam(Long id) {
        TeamEntity entity = getTeam(id);
        teamRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogItemResponseDTO> getHomePrashes() {
        return homePrasheRepository.findAll().stream()
                .map(this::mapHomePrashe)
                .toList();
    }

    @Override
    public CatalogItemResponseDTO generateHomePrashe() {
        List<HomePrasheEntity> homePrasheRepositories = homePrasheRepository.findAll();
        if (homePrasheRepositories.isEmpty()) {
            throw new GenericException(ResponseCode.NOT_FOUND.getCode(), "No hay home prashes disponibles");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(homePrasheRepositories.size());
        HomePrasheEntity randomHomePrashe = homePrasheRepositories.get(randomIndex);
        return mapHomePrashe(randomHomePrashe);

    }

    @Override
    public CatalogItemResponseDTO createHomePrashe(CatalogItemRequestDTO request) {
        String normalizedName = normalizeName(request.getName());
        homePrasheRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Ya existe un home prashe con ese texto");
                });

        HomePrasheEntity entity = new HomePrasheEntity();
        entity.setName(normalizedName);
        entity.setActive(resolveActive(request.getActive()));
        return mapHomePrashe(homePrasheRepository.save(entity));
    }

    @Override
    public CatalogItemResponseDTO updateHomePrashe(Long id, CatalogItemRequestDTO request) {
        HomePrasheEntity entity = getHomePrashe(id);
        String normalizedName = normalizeName(request.getName());
        homePrasheRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new GenericException(ResponseCode.BAD_REQUEST.getCode(), "Ya existe un home prashe con ese texto");
                });

        entity.setName(normalizedName);
        entity.setActive(resolveActive(request.getActive()));
        return mapHomePrashe(homePrasheRepository.save(entity));
    }

    @Override
    public void deleteHomePrashe(Long id) {
        HomePrasheEntity entity = getHomePrashe(id);
        homePrasheRepository.delete(entity);
    }

    private CatalogItemResponseDTO mapSport(SportEntity entity) {
        return CatalogItemResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.getActive())
                .logoUrl(catalogLogoStorageService.resolvePublicUrl(entity.getLogoKey()))
                .build();
    }

    private CatalogItemResponseDTO mapCountry(CountryEntity entity) {
        return CatalogItemResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.getActive())
                .logoUrl(catalogLogoStorageService.resolvePublicUrl(entity.getLogoKey()))
                .build();
    }

    private CompetitionResponseDTO mapCompetition(CompetitionEntity entity) {
        return CompetitionResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.getActive())
                .logoUrl(catalogLogoStorageService.resolvePublicUrl(entity.getLogoKey()))
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
                .logoUrl(catalogLogoStorageService.resolvePublicUrl(entity.getLogoKey()))
                .competitionId(competition.getId())
                .competitionName(competition.getName())
                .sportId(competition.getSport().getId())
                .sportName(competition.getSport().getName())
                .countryId(competition.getCountry().getId())
                .countryName(competition.getCountry().getName())
                .build();
    }

    private CatalogItemResponseDTO mapHomePrashe(HomePrasheEntity entity) {
        return CatalogItemResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.getActive())
                .logoUrl(null)
                .build();
    }

    private SportEntity getSport(Long id) {
        return sportRepository.findById(id)
                .orElseThrow(() -> new GenericException(ResponseCode.NOT_FOUND.getCode(), "Deporte no encontrado"));
    }

    private CountryEntity getCountry(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new GenericException(ResponseCode.NOT_FOUND.getCode(), "País no encontrado"));
    }

    private CompetitionEntity getCompetition(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new GenericException(ResponseCode.NOT_FOUND.getCode(), "Competición no encontrada"));
    }

    private TeamEntity getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new GenericException(ResponseCode.NOT_FOUND.getCode(), "Equipo no encontrado"));
    }

    private HomePrasheEntity getHomePrashe(Long id) {
        return homePrasheRepository.findById(id)
                .orElseThrow(() -> new GenericException(ResponseCode.NOT_FOUND.getCode(), "Home prashe no encontrado"));
    }

    private String normalizeName(String value) {
        return value == null ? null : value.trim();
    }

    private Boolean resolveActive(Boolean active) {
        return active == null || active;
    }
}
