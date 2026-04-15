package com.samuel_mc.pickados_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserRequestDTO {
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 160, message = "El apellido no puede exceder 160 caracteres")
    private String lastname;

    @Size(max = 50, message = "El username no puede exceder 50 caracteres")
    private String username;

    @Email(message = "El email no es válido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;
}
