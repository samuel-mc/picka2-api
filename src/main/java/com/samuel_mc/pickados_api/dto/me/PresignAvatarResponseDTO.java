package com.samuel_mc.pickados_api.dto.me;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignAvatarResponseDTO {
    private String uploadUrl;
    private String objectKey;
}
