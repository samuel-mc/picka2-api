package com.samuel_mc.pickados_api.dto.me;

import lombok.Data;

@Data
public class MeProfileResponseDTO {
    private long id;
    private String name;
    private String lastname;
    private String email;
    private String bio;
    private String avatarUrl;
}
