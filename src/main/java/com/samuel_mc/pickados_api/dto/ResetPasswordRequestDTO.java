package com.samuel_mc.pickados_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ResetPasswordRequestDTO {

    @NotNull(message = "El token es obligatorio")
    @NotEmpty(message = "El token es obligatorio")
    private String token;

    @NotNull(message = "La nueva contraseña es obligatoria")
    @NotEmpty(message = "La nueva contraseña es obligatoria")
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
