package com.samuel_mc.pickados_api.controllers.post;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.post.SportsbookResponseDTO;
import com.samuel_mc.pickados_api.service.post.SportsbookService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sportsbooks")
@Tag(name = "Sportsbooks", description = "Catálogo de casas de apuesta")
public class SportsbookController {

    private final SportsbookService sportsbookService;
    private final ResponseUtils responseUtils;

    public SportsbookController(SportsbookService sportsbookService, ResponseUtils responseUtils) {
        this.sportsbookService = sportsbookService;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDTO<List<SportsbookResponseDTO>>> getAll() {
        return responseUtils.generateSuccessResponse(sportsbookService.getAll());
    }
}
