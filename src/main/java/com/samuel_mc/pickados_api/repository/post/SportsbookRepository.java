package com.samuel_mc.pickados_api.repository.post;

import com.samuel_mc.pickados_api.entity.SportsbookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SportsbookRepository extends JpaRepository<SportsbookEntity, Long> {
    List<SportsbookEntity> findAllByOrderByNameAsc();
    Optional<SportsbookEntity> findByNameIgnoreCase(String name);
}
