package com.samuel_mc.pickados_api.dto.me;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteAvatarRequestDTO {

    @NotBlank
    private String objectKey;
}
