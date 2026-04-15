package com.samuel_mc.pickados_api.controllers.analytics;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.analytics.AdminAnalyticsResponseDTO;
import com.samuel_mc.pickados_api.service.analytics.AdminAnalyticsService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/admin")
@Tag(name = "Admin Analytics", description = "Estadísticas y métricas administrativas")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;
    private final ResponseUtils responseUtils;

    public AdminAnalyticsController(AdminAnalyticsService adminAnalyticsService, ResponseUtils responseUtils) {
        this.adminAnalyticsService = adminAnalyticsService;
        this.responseUtils = responseUtils;
    }

    @GetMapping("/overview")
    public ResponseEntity<GenericResponseDTO<AdminAnalyticsResponseDTO>> getOverview() {
        return responseUtils.generateSuccessResponse(adminAnalyticsService.getOverview());
    }
}
