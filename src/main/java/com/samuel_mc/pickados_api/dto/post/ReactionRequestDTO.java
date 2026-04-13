package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.ReactionType;
import jakarta.validation.constraints.NotNull;

public class ReactionRequestDTO {

    @NotNull(message = "El tipo de reacción es obligatorio")
    private ReactionType type;

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }
}
