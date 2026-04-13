package com.samuel_mc.pickados_api.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequestDTO {

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 1500, message = "El comentario no puede exceder 1500 caracteres")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
