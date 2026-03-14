package com.samuel_mc.pickados_api.repository;

import com.samuel_mc.pickados_api.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
    List<RoleEntity> findAllByOrderByNameAsc();
}