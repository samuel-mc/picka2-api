package com.samuel_mc.pickados_api.dto.post;

public class ToggleStateResponseDTO {
    private boolean active;

    public ToggleStateResponseDTO() {
    }

    public ToggleStateResponseDTO(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
