package com.samuel_mc.pickados_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegisterTipsterRequestDTO extends RegisterUserRequestDTO {
    private String bio;
    private String avatarUrl;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @AssertTrue(message = "El tipster debe ser mayor de 18 años")
    public boolean isAdultTipster() {
        return birthDate != null && !birthDate.isAfter(LocalDate.now().minusYears(18));
    }
}
