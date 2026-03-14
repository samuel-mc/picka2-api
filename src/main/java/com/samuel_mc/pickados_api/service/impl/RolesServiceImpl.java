package com.samuel_mc.pickados_api.service.impl;

import com.samuel_mc.pickados_api.dto.RoleDTO;
import com.samuel_mc.pickados_api.entity.RoleEntity;
import com.samuel_mc.pickados_api.repository.RoleRepository;
import com.samuel_mc.pickados_api.service.RolesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolesServiceImpl implements RolesService {

    private final RoleRepository roleRepository;

    public RolesServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public RoleDTO getDTOFromEntity(RoleEntity roleEntity) {
        if (roleEntity == null) return new RoleDTO();
        return new RoleDTO(roleEntity.getId(), roleEntity.getName());
    }

    @Override
    public List<RoleDTO> getDTOFromEntity(List<RoleEntity> roleEntity) {
        List<RoleDTO> data = new ArrayList<>();
        if (roleEntity == null) return data;
        for (RoleEntity role: roleEntity) {
            data.add(this.getDTOFromEntity(role));
        }
        return data;
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return this.getDTOFromEntity(this.roleRepository.findAllByOrderByNameAsc());
    }
}
