package com.samuel_mc.pickados_api.controllers;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.RoleDTO;
import com.samuel_mc.pickados_api.service.RolesService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolesController {

    private final ResponseUtils responseUtils;
    private final RolesService rolesService;

    public RolesController(ResponseUtils responseUtils, RolesService rolesService) {
        this.responseUtils = responseUtils;
        this.rolesService = rolesService;
    }

    @GetMapping("")
    public ResponseEntity<GenericResponseDTO<List<RoleDTO>>> getAllRoles() {
        return this.responseUtils.generateSuccessResponse(this.rolesService.getAllRoles());
    }
}
