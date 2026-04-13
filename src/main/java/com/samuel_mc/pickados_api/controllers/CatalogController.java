package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.CatalogItemRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CatalogItemResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompleteCatalogLogoRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.CompetitionResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.PresignCatalogLogoRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.PresignCatalogLogoResponseDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamRequestDTO;
import com.samuel_mc.pickados_api.dto.catalog.TeamResponseDTO;
import com.samuel_mc.pickados_api.service.CatalogService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalogs")
@Tag(name = "Catálogos", description = "Gestión de catálogos administrativos")
public class CatalogController {

    private final CatalogService catalogService;
    private final ResponseUtils responseUtils;

    public CatalogController(CatalogService catalogService, ResponseUtils responseUtils) {
        this.catalogService = catalogService;
        this.responseUtils = responseUtils;
    }

    @GetMapping("/sports")
    public ResponseEntity<GenericResponseDTO<List<CatalogItemResponseDTO>>> getSports() {
        return responseUtils.generateSuccessResponse(catalogService.getSports());
    }

    @PostMapping("/sports")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> createSport(
            @RequestBody @Valid CatalogItemRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.createSport(request));
    }

    @PutMapping("/sports/{id}")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> updateSport(
            @PathVariable Long id,
            @RequestBody @Valid CatalogItemRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.updateSport(id, request));
    }

    @PostMapping("/sports/{id}/logo/presign")
    public ResponseEntity<GenericResponseDTO<PresignCatalogLogoResponseDTO>> presignSportLogo(
            @PathVariable Long id,
            @RequestBody @Valid PresignCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.presignSportLogo(id, request.getContentType()));
    }

    @PostMapping("/sports/{id}/logo/complete")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> completeSportLogo(
            @PathVariable Long id,
            @RequestBody @Valid CompleteCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.completeSportLogo(id, request));
    }

    @DeleteMapping("/sports/{id}")
    public ResponseEntity<GenericResponseDTO<String>> deleteSport(@PathVariable Long id) {
        catalogService.deleteSport(id);
        return responseUtils.generateSuccessResponse("Deporte eliminado correctamente");
    }

    @GetMapping("/countries")
    public ResponseEntity<GenericResponseDTO<List<CatalogItemResponseDTO>>> getCountries() {
        return responseUtils.generateSuccessResponse(catalogService.getCountries());
    }

    @PostMapping("/countries")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> createCountry(
            @RequestBody @Valid CatalogItemRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.createCountry(request));
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> updateCountry(
            @PathVariable Long id,
            @RequestBody @Valid CatalogItemRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.updateCountry(id, request));
    }

    @PostMapping("/countries/{id}/logo/presign")
    public ResponseEntity<GenericResponseDTO<PresignCatalogLogoResponseDTO>> presignCountryLogo(
            @PathVariable Long id,
            @RequestBody @Valid PresignCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.presignCountryLogo(id, request.getContentType()));
    }

    @PostMapping("/countries/{id}/logo/complete")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> completeCountryLogo(
            @PathVariable Long id,
            @RequestBody @Valid CompleteCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.completeCountryLogo(id, request));
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<GenericResponseDTO<String>> deleteCountry(@PathVariable Long id) {
        catalogService.deleteCountry(id);
        return responseUtils.generateSuccessResponse("País eliminado correctamente");
    }

    @GetMapping("/competitions")
    public ResponseEntity<GenericResponseDTO<List<CompetitionResponseDTO>>> getCompetitions() {
        return responseUtils.generateSuccessResponse(catalogService.getCompetitions());
    }

    @PostMapping("/competitions")
    public ResponseEntity<GenericResponseDTO<CompetitionResponseDTO>> createCompetition(
            @RequestBody @Valid CompetitionRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.createCompetition(request));
    }

    @PutMapping("/competitions/{id}")
    public ResponseEntity<GenericResponseDTO<CompetitionResponseDTO>> updateCompetition(
            @PathVariable Long id,
            @RequestBody @Valid CompetitionRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.updateCompetition(id, request));
    }

    @PostMapping("/competitions/{id}/logo/presign")
    public ResponseEntity<GenericResponseDTO<PresignCatalogLogoResponseDTO>> presignCompetitionLogo(
            @PathVariable Long id,
            @RequestBody @Valid PresignCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.presignCompetitionLogo(id, request.getContentType()));
    }

    @PostMapping("/competitions/{id}/logo/complete")
    public ResponseEntity<GenericResponseDTO<CompetitionResponseDTO>> completeCompetitionLogo(
            @PathVariable Long id,
            @RequestBody @Valid CompleteCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.completeCompetitionLogo(id, request));
    }

    @DeleteMapping("/competitions/{id}")
    public ResponseEntity<GenericResponseDTO<String>> deleteCompetition(@PathVariable Long id) {
        catalogService.deleteCompetition(id);
        return responseUtils.generateSuccessResponse("Competición eliminada correctamente");
    }

    @GetMapping("/teams")
    public ResponseEntity<GenericResponseDTO<List<TeamResponseDTO>>> getTeams() {
        return responseUtils.generateSuccessResponse(catalogService.getTeams());
    }

    @PostMapping("/teams")
    public ResponseEntity<GenericResponseDTO<TeamResponseDTO>> createTeam(
            @RequestBody @Valid TeamRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.createTeam(request));
    }

    @PutMapping("/teams/{id}")
    public ResponseEntity<GenericResponseDTO<TeamResponseDTO>> updateTeam(
            @PathVariable Long id,
            @RequestBody @Valid TeamRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.updateTeam(id, request));
    }

    @PostMapping("/teams/{id}/logo/presign")
    public ResponseEntity<GenericResponseDTO<PresignCatalogLogoResponseDTO>> presignTeamLogo(
            @PathVariable Long id,
            @RequestBody @Valid PresignCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.presignTeamLogo(id, request.getContentType()));
    }

    @PostMapping("/teams/{id}/logo/complete")
    public ResponseEntity<GenericResponseDTO<TeamResponseDTO>> completeTeamLogo(
            @PathVariable Long id,
            @RequestBody @Valid CompleteCatalogLogoRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.completeTeamLogo(id, request));
    }

    @DeleteMapping("/teams/{id}")
    public ResponseEntity<GenericResponseDTO<String>> deleteTeam(@PathVariable Long id) {
        catalogService.deleteTeam(id);
        return responseUtils.generateSuccessResponse("Equipo eliminado correctamente");
    }

    @GetMapping("/home-prashes")
    public ResponseEntity<GenericResponseDTO<List<CatalogItemResponseDTO>>> getHomePrashes() {
        return responseUtils.generateSuccessResponse(catalogService.getHomePrashes());
    }

    @GetMapping("/generate-home-prashe")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> generateHomePrashe() {
        return responseUtils.generateSuccessResponse(catalogService.generateHomePrashe());
    }

    @PostMapping("/home-prashes")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> createHomePrashe(
            @RequestBody @Valid CatalogItemRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.createHomePrashe(request));
    }

    @PutMapping("/home-prashes/{id}")
    public ResponseEntity<GenericResponseDTO<CatalogItemResponseDTO>> updateHomePrashe(
            @PathVariable Long id,
            @RequestBody @Valid CatalogItemRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(catalogService.updateHomePrashe(id, request));
    }

    @DeleteMapping("/home-prashes/{id}")
    public ResponseEntity<GenericResponseDTO<String>> deleteHomePrashe(@PathVariable Long id) {
        catalogService.deleteHomePrashe(id);
        return responseUtils.generateSuccessResponse("Home prashe eliminado correctamente");
    }
}
