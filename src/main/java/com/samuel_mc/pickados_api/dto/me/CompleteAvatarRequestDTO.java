package com.samuel_mc.pickados_api.dto.me;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompleteAvatarRequestDTO {

    @NotBlank
    @Size(max = 512, message = "La clave del avatar es demasiado larga")
    private String objectKey;
}
