package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.dto.referrals.MyReferralResponseDTO;
import com.samuel_mc.pickados_api.entity.ReferralCodeEntity;
import com.samuel_mc.pickados_api.entity.ReferralEntity;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.repository.ReferralCodeRepository;
import com.samuel_mc.pickados_api.repository.ReferralRepository;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class ReferralService {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RNG = new SecureRandom();

    private final ReferralCodeRepository referralCodeRepository;
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final TipsterProfileRepository tipsterProfileRepository;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public ReferralService(
            ReferralCodeRepository referralCodeRepository,
            ReferralRepository referralRepository,
            UserRepository userRepository,
            TipsterProfileRepository tipsterProfileRepository
    ) {
        this.referralCodeRepository = referralCodeRepository;
        this.referralRepository = referralRepository;
        this.userRepository = userRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
    }

    @Transactional
    public MyReferralResponseDTO getOrCreateMyCode(long userId) {
        UserEntity user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ReferralCodeEntity code = referralCodeRepository.findByUser(user).orElse(null);
        if (code == null) {
            code = new ReferralCodeEntity();
            code.setUser(user);
            code.setCode(generateUniqueCode());
            code = referralCodeRepository.save(code);
        }

        String shareUrl = frontendUrl.replaceAll("/$", "") + "/r/" + code.getCode();
        return new MyReferralResponseDTO(code.getCode(), shareUrl);
    }

    @Transactional(readOnly = true)
    public Long resolveInviterUserId(String code) {
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido");
        }
        ReferralCodeEntity entity = referralCodeRepository.findByCode(code.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código no encontrado"));
        return entity.getUser().getId();
    }

    @Transactional
    public void attachReferralOnSignup(UserEntity invitedUser, String referralCode) {
        if (referralCode == null || referralCode.isBlank()) return;

        String normalized = referralCode.trim().toUpperCase(Locale.ROOT);
        ReferralCodeEntity code = referralCodeRepository.findByCode(normalized).orElse(null);
        if (code == null) return;

        UserEntity inviter = code.getUser();
        if (inviter == null) return;
        if (inviter.getId() == invitedUser.getId()) return;

        if (referralRepository.findByInvitedId(invitedUser.getId()).isPresent()) {
            return;
        }

        ReferralEntity referral = new ReferralEntity();
        referral.setInviter(inviter);
        referral.setInvited(invitedUser);
        referralRepository.save(referral);
    }

    @Transactional
    public void markActivatedIfEligible(long invitedUserId, long followsCount) {
        if (followsCount < 3) {
            return;
        }
        ReferralEntity referral = referralRepository.findByInvitedId(invitedUserId).orElse(null);
        if (referral == null || referral.getActivatedAt() != null) {
            return;
        }

        referral.setActivatedAt(LocalDateTime.now());
        referralRepository.save(referral);

        // Recompensa A+B al invitador: badge + boost 48h
        TipsterProfileEntity inviterProfile = tipsterProfileRepository.findByUser(referral.getInviter()).orElse(null);
        if (inviterProfile != null) {
            inviterProfile.setReferralBadge(true);
            inviterProfile.setBoostUntil(LocalDateTime.now().plusHours(48));
            tipsterProfileRepository.save(inviterProfile);
        }
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String candidate = randomCode(8);
            if (referralCodeRepository.findByCode(candidate).isEmpty()) {
                return candidate;
            }
        }
        // fallback: 10 chars
        String candidate = randomCode(10);
        if (referralCodeRepository.findByCode(candidate).isEmpty()) {
            return candidate;
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo generar código de referido");
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}

