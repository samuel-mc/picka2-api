package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.dto.RoleDTO;
import com.samuel_mc.pickados_api.entity.RoleEntity;

import java.util.List;

public interface RolesService {
    /**
     * Get a RoleDTO object from a RoleEntity kind
     */
    RoleDTO getDTOFromEntity(RoleEntity roleEntity);

    /**
     * Get a RoleDTO list from a RoleEntity list
     */
    List<RoleDTO> getDTOFromEntity(List<RoleEntity> roleEntity);

    /**
     * Get all roles in the database
     * @return the roles list
     */
    List<RoleDTO> getAllRoles();
}
