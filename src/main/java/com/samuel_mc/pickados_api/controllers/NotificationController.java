package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.notification.NotificationListResponseDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.service.notification.NotificationService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ResponseUtils responseUtils;

    public NotificationController(NotificationService notificationService, ResponseUtils responseUtils) {
        this.notificationService = notificationService;
        this.responseUtils = responseUtils;
    }

    @GetMapping
    public ResponseEntity<GenericResponseDTO<NotificationListResponseDTO>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return responseUtils.generateSuccessResponse(notificationService.getNotifications(requireUserId(principal)));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<GenericResponseDTO<String>> markAsRead(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(requireUserId(principal), notificationId);
        return responseUtils.generateSuccessResponse("Notificación actualizada");
    }

    private Long requireUserId(CustomUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario no autenticado");
        }
        return principal.getId();
    }
}
