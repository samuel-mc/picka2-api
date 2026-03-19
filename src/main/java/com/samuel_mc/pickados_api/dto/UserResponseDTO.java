package com.samuel_mc.pickados_api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private long id;
    private String name;
    private String lastname;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
