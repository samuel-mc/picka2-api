package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.referrals.MyReferralResponseDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.service.ReferralService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/referrals")
@Tag(name = "Referidos", description = "Invitaciones y referidos")
public class ReferralController {
    private final ReferralService referralService;
    private final ResponseUtils responseUtils;

    public ReferralController(ReferralService referralService, ResponseUtils responseUtils) {
        this.referralService = referralService;
        this.responseUtils = responseUtils;
    }

    @GetMapping("/me")
    public ResponseEntity<GenericResponseDTO<MyReferralResponseDTO>> myCode(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return responseUtils.generateSuccessResponse(referralService.getOrCreateMyCode(principal.getId()));
    }

    @GetMapping("/resolve/{code}")
    public ResponseEntity<?> resolve(@PathVariable String code) {
        Long inviterUserId = referralService.resolveInviterUserId(code);
        return ResponseEntity.ok(Map.of("inviterUserId", inviterUserId));
    }
}

