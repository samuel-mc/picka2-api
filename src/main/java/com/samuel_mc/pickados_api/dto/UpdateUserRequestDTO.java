package com.samuel_mc.pickados_api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserRequestDTO {
    private String name;
    private String lastname;
    private String username;
    private String email;
}
