package com.samuel_mc.pickados_api.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PresignPostMediaRequestDTO {

    @NotBlank
    @Pattern(regexp = "image/(jpeg|png|webp)")
    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
