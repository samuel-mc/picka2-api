package com.samuel_mc.pickados_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterUserRequestDTO {

    @NotNull(message = "El nombre es obligatorio")
    @NotEmpty(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El username es obligatorio")
    @NotEmpty(message = "El username es obligatorio")
    private String username;

    @NotNull(message = "El email es obligatorio")
    @NotEmpty(message = "El email es obligatorio")
    private String email;

    @NotNull(message = "El password es obligatorio")
    @NotEmpty(message = "El password es obligatorio")
    private String password;

}
