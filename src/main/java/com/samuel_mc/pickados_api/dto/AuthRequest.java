package com.samuel_mc.pickados_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class AuthRequest {
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    public @NotNull @NotEmpty String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @NotEmpty String username) {
        this.username = username;
    }

    public @NotNull @NotEmpty String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotEmpty String password) {
        this.password = password;
    }
}

