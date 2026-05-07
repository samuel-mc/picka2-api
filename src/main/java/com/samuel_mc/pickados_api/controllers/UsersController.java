package com.samuel_mc.pickados_api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import java.util.Locale;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.dto.mappers.UserMapper;
import com.samuel_mc.pickados_api.dto.UserResponseDTO;
import com.samuel_mc.pickados_api.dto.UpdateUserRequestDTO;
import com.samuel_mc.pickados_api.dto.user.PublicProfileResponseDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UsersController {

    private static final String LEGACY_USER_ROLE = "USER";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private TipsterProfileRepository tipsterProfileRepository;

    @GetMapping
    public List<UserResponseDTO> getUsers(@org.springframework.web.bind.annotation.RequestParam(required = false) String role) {
        List<UserEntity> users = normalizeRole(role)
                .map(userRepository::findByRole_NameAndDeletedFalseOrderByCreatedAtDesc)
                .orElseGet(() -> userRepository.findByRole_NameNotAndDeletedFalseOrderByCreatedAtDesc(LEGACY_USER_ROLE));

        return users.stream()
                .map(this::mapUserResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/admins")
    public List<UserResponseDTO> getAdmins() {
        return userRepository.findByRole_NameAndDeletedFalseOrderByCreatedAtDesc("ADMIN").stream()
                .map(this::mapUserResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<PublicProfileResponseDTO> getPublicProfile(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userProfileService.getPublicProfile(principal.getId(), id));
    }

    @GetMapping("/public/{id}/profile")
    public ResponseEntity<PublicProfileResponseDTO> getPublicProfileAnonymous(@PathVariable Long id) {
        // currentUserId=0 -> not a real user id, yields followedByCurrentUser=false and selfProfile=false
        return ResponseEntity.ok(userProfileService.getPublicProfile(0L, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequestDTO updateDTO) {
        Optional<UserEntity> userOpt = userRepository.findByIdAndDeletedFalse(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (updateDTO.getName() != null && !updateDTO.getName().isBlank()) user.setName(updateDTO.getName().trim());
            if (updateDTO.getLastname() != null && !updateDTO.getLastname().isBlank()) user.setLastname(updateDTO.getLastname().trim());
            if (updateDTO.getUsername() != null && !updateDTO.getUsername().isBlank()) user.setUsername(updateDTO.getUsername().trim());
            if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) user.setEmail(updateDTO.getEmail().trim());
            userRepository.save(user);
            return ResponseEntity.ok(mapUserResponse(user));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/tipster-validation")
    public ResponseEntity<UserResponseDTO> toggleTipsterValidation(@PathVariable Long id) {
        Optional<UserEntity> userOpt = userRepository.findByIdAndDeletedFalse(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserEntity user = userOpt.get();
        if (user.getRole() == null || !"TIPSTER".equalsIgnoreCase(user.getRole().getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Optional<TipsterProfileEntity> profileOpt = tipsterProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        TipsterProfileEntity profile = profileOpt.get();
        profile.setValidated(!Boolean.TRUE.equals(profile.getValidated()));
        tipsterProfileRepository.save(profile);
        return ResponseEntity.ok(mapUserResponse(user));
    }

    @PutMapping("/{id}/inactivate")
    public ResponseEntity<Void> inactivateUser(@PathVariable Long id) {
        Optional<UserEntity> userOpt = userRepository.findByIdAndDeletedFalse(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            // Toggle the active flag
            user.setActive(!user.getActive());
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<UserEntity> userOpt = userRepository.findByIdAndDeletedFalse(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setDeleted(true);
            user.setActive(false);
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private Optional<String> normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(role.trim().toUpperCase(Locale.ROOT));
    }

    private UserResponseDTO mapUserResponse(UserEntity user) {
        UserResponseDTO dto = UserMapper.INSTANCIA.userEntityToUserResponseDTO(user);
        boolean validatedTipster = tipsterProfileRepository.findByUser(user)
                .map(TipsterProfileEntity::getValidated)
                .filter(Boolean.TRUE::equals)
                .isPresent();
        dto.setValidatedTipster(validatedTipster);
        return dto;
    }

}
