package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.me.CompleteAvatarRequestDTO;
import com.samuel_mc.pickados_api.dto.me.MeProfileResponseDTO;
import com.samuel_mc.pickados_api.dto.me.PresignAvatarRequestDTO;
import com.samuel_mc.pickados_api.dto.me.PresignAvatarResponseDTO;
import com.samuel_mc.pickados_api.dto.me.UpdateMeProfileRequestDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.service.MeProfileService;
import com.samuel_mc.pickados_api.service.ProfileAvatarStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/me/profile")
@Tag(name = "Mi perfil", description = "Perfil del usuario autenticado")
public class MeProfileController {

    private final MeProfileService meProfileService;
    private final ProfileAvatarStorageService profileAvatarStorageService;

    public MeProfileController(MeProfileService meProfileService, ProfileAvatarStorageService profileAvatarStorageService) {
        this.meProfileService = meProfileService;
        this.profileAvatarStorageService = profileAvatarStorageService;
    }

    @GetMapping
    public MeProfileResponseDTO get(@AuthenticationPrincipal CustomUserDetails principal) {
        return meProfileService.getProfile(principal.getId());
    }

    @PutMapping
    public MeProfileResponseDTO update(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid UpdateMeProfileRequestDTO body) {
        return meProfileService.updateProfile(principal.getId(), body);
    }

    @PostMapping("/avatar/presign")
    public ResponseEntity<?> presignAvatar(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody PresignAvatarRequestDTO body) {
        try {
            PresignAvatarResponseDTO dto = profileAvatarStorageService.presignPut(principal.getId(), body.getContentType());
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(503).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/avatar/complete")
    public MeProfileResponseDTO completeAvatar(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CompleteAvatarRequestDTO body) {
        return meProfileService.completeAvatarUpload(principal.getId(), body);
    }
}
