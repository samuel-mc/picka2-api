package com.samuel_mc.pickados_api.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompletePostMediaRequestDTO {

    @NotBlank
    @Size(max = 1024, message = "La clave del archivo es demasiado larga")
    private String objectKey;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}
