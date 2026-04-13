package com.samuel_mc.pickados_api.dto.post;

import jakarta.validation.constraints.Size;

public class SharePostRequestDTO {

    @Size(max = 60, message = "El canal de share no puede exceder 60 caracteres")
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
