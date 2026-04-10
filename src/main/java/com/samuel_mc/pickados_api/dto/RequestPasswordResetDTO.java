package com.samuel_mc.pickados_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RequestPasswordResetDTO {

    @NotNull(message = "El email es obligatorio")
    @NotEmpty(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
