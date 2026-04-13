package com.samuel_mc.pickados_api.dto.post;

import jakarta.validation.constraints.NotBlank;

public class CompletePostMediaRequestDTO {

    @NotBlank
    private String objectKey;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}
