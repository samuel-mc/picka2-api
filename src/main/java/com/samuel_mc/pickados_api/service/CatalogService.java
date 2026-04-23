package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.dto.catalog.CatalogItemRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CatalogItemResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompleteCatalogLogoRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.PresignCatalogLogoResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.SportsbookCatalogRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.SportsbookCatalogResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;

import java.util.List;

public interface CatalogService {
    List<CatalogItemResponseDTO> getSports();
    CatalogItemResponseDTO createSport(CatalogItemRequestDTO request);
    CatalogItemResponseDTO updateSport(Long id, CatalogItemRequestDTO request);
    PresignCatalogLogoResponseDTO presignSportLogo(Long id, String contentType);
    CatalogItemResponseDTO completeSportLogo(Long id, CompleteCatalogLogoRequestDTO request);
    void deleteSport(Long id);

    List<CatalogItemResponseDTO> getCountries();
    CatalogItemResponseDTO createCountry(CatalogItemRequestDTO request);
    CatalogItemResponseDTO updateCountry(Long id, CatalogItemRequestDTO request);
    PresignCatalogLogoResponseDTO presignCountryLogo(Long id, String contentType);
    CatalogItemResponseDTO completeCountryLogo(Long id, CompleteCatalogLogoRequestDTO request);
    void deleteCountry(Long id);

    List<CompetitionResponseDTO> getCompetitions();
    CompetitionResponseDTO createCompetition(CompetitionRequestDTO request);
    CompetitionResponseDTO updateCompetition(Long id, CompetitionRequestDTO request);
    PresignCatalogLogoResponseDTO presignCompetitionLogo(Long id, String contentType);
    CompetitionResponseDTO completeCompetitionLogo(Long id, CompleteCatalogLogoRequestDTO request);
    void deleteCompetition(Long id);

    List<TeamResponseDTO> getTeams();
    TeamResponseDTO createTeam(TeamRequestDTO request);
    TeamResponseDTO updateTeam(Long id, TeamRequestDTO request);
    PresignCatalogLogoResponseDTO presignTeamLogo(Long id, String contentType);
    TeamResponseDTO completeTeamLogo(Long id, CompleteCatalogLogoRequestDTO request);
    void deleteTeam(Long id);

    List<SportsbookCatalogResponseDTO> getSportsbooks();
    SportsbookCatalogResponseDTO createSportsbook(SportsbookCatalogRequestDTO request);
    SportsbookCatalogResponseDTO updateSportsbook(Long id, SportsbookCatalogRequestDTO request);
    PresignCatalogLogoResponseDTO presignSportsbookLogo(Long id, String contentType);
    SportsbookCatalogResponseDTO completeSportsbookLogo(Long id, CompleteCatalogLogoRequestDTO request);
    void deleteSportsbook(Long id);

    List<CatalogItemResponseDTO> getHomePrashes();
    CatalogItemResponseDTO generateHomePrashe();
    CatalogItemResponseDTO createHomePrashe(CatalogItemRequestDTO request);
    CatalogItemResponseDTO updateHomePrashe(Long id, CatalogItemRequestDTO request);
    void deleteHomePrashe(Long id);
}
