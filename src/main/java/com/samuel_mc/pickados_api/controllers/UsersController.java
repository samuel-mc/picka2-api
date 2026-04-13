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

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.dto.mappers.UserMapper;
import com.samuel_mc.pickados_api.dto.UserResponseDTO;
import com.samuel_mc.pickados_api.dto.UpdateUserRequestDTO;
import com.samuel_mc.pickados_api.dto.user.PublicProfileResponseDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.service.UserProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/admins")
    public List<UserResponseDTO> getAdmins() {
        return userRepository.findByRole_NameAndDeletedFalse("ADMIN").stream()
                .map(UserMapper.INSTANCIA::userEntityToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<PublicProfileResponseDTO> getPublicProfile(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userProfileService.getPublicProfile(principal.getId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDTO updateDTO) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (updateDTO.getName() != null) user.setName(updateDTO.getName());
            if (updateDTO.getLastname() != null) user.setLastname(updateDTO.getLastname());
            if (updateDTO.getUsername() != null) user.setUsername(updateDTO.getUsername());
            if (updateDTO.getEmail() != null) user.setEmail(updateDTO.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok(UserMapper.INSTANCIA.userEntityToUserResponseDTO(user));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/inactivate")
    public ResponseEntity<Void> inactivateUser(@PathVariable Long id) {
        Optional<UserEntity> userOpt = userRepository.findById(id);
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
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setDeleted(true);
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
