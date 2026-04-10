package com.samuel_mc.pickados_api.dto.me;

import lombok.Data;

@Data
public class UpdateMeProfileRequestDTO {
    private String name;
    private String lastname;
    private String bio;
}
