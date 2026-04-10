package com.samuel_mc.pickados_api.dto.me;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PresignAvatarRequestDTO {

    @NotBlank
    @Pattern(regexp = "image/(jpeg|png|webp)")
    private String contentType;
}
