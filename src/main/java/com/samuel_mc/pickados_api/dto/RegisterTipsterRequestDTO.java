package com.samuel_mc.pickados_api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterTipsterRequestDTO extends RegisterUserRequestDTO {
    private String bio;
    private String avatarUrl;
}
