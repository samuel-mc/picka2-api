package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.dto.me.CompleteAvatarRequestDTO;
import com.samuel_mc.pickados_api.dto.me.MeProfileResponseDTO;
import com.samuel_mc.pickados_api.dto.me.UpdateMeProfileRequestDTO;
import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class MeProfileService {

    private final UserRepository userRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final ProfileAvatarStorageService profileAvatarStorageService;
    private final R2Properties r2Properties;

    public MeProfileService(UserRepository userRepository,
            TipsterProfileRepository tipsterProfileRepository,
            ProfileAvatarStorageService profileAvatarStorageService,
            R2Properties r2Properties) {
        this.userRepository = userRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.profileAvatarStorageService = profileAvatarStorageService;
        this.r2Properties = r2Properties;
    }

    @Transactional(readOnly = true)
    public MeProfileResponseDTO getProfile(long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        MeProfileResponseDTO dto = new MeProfileResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());

        if (isTipster(user)) {
            TipsterProfileEntity profile = tipsterProfileRepository.findByUser(user).orElse(null);
            dto.setBio(profile != null ? profile.getBio() : null);
            dto.setAvatarUrl(resolveAvatarUrl(profile != null ? profile.getAvatarUrl() : null));
        } else {
            dto.setBio(user.getBio());
            dto.setAvatarUrl(resolveAvatarUrl(user.getProfilePhotoKey()));
        }
        return dto;
    }

    @Transactional
    public MeProfileResponseDTO updateProfile(long userId, UpdateMeProfileRequestDTO body) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (body.getName() != null && !body.getName().isBlank()) {
            user.setName(body.getName().trim());
        }
        if (body.getLastname() != null && !body.getLastname().isBlank()) {
            user.setLastname(body.getLastname().trim());
        }
        if (body.getBio() != null) {
            String bioVal = body.getBio().isBlank() ? null : body.getBio().trim();
            if (isTipster(user)) {
                TipsterProfileEntity profile = tipsterProfileRepository.findByUser(user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de tipster no encontrado"));
                profile.setBio(bioVal);
                tipsterProfileRepository.save(profile);
            } else {
                user.setBio(bioVal);
            }
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return getProfile(userId);
    }

    @Transactional
    public MeProfileResponseDTO completeAvatarUpload(long userId, CompleteAvatarRequestDTO body) {
        if (!profileAvatarStorageService.isKeyOwnedByUser(userId, body.getObjectKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clave de objeto no válida para este usuario");
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (isTipster(user)) {
            TipsterProfileEntity profile = tipsterProfileRepository.findByUser(user)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil de tipster no encontrado"));
            profile.setAvatarUrl(body.getObjectKey());
            tipsterProfileRepository.save(profile);
        } else {
            user.setProfilePhotoKey(body.getObjectKey());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
        return getProfile(userId);
    }

    private boolean isTipster(UserEntity user) {
        return user.getRole() != null && "TIPSTER".equalsIgnoreCase(user.getRole().getName());
    }

    private String resolveAvatarUrl(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }
        String s = stored.trim();
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return s;
        }
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            return null;
        }
        return base.replaceAll("/$", "") + "/" + s;
    }
}
