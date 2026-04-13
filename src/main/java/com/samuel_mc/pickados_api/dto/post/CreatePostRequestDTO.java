package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.PostType;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CreatePostRequestDTO {

    @NotNull(message = "El tipo de post es obligatorio")
    private PostType type;

    @NotBlank(message = "El contenido es obligatorio")
    @Size(max = 4000, message = "El contenido no puede exceder 4000 caracteres")
    private String content;

    @Size(max = 1024, message = "La clave de imagen es demasiado larga")
    private String imageKey;

    private List<@Size(max = 80, message = "Cada tag debe tener máximo 80 caracteres") String> tags = new ArrayList<>();

    @NotNull(message = "La visibilidad es obligatoria")
    private PostVisibility visibility = PostVisibility.PUBLIC;

    @Valid
    private PostPickRequestDTO simplePick;

    @Valid
    private PostParleyRequestDTO parley;

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public PostVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }

    public PostPickRequestDTO getSimplePick() {
        return simplePick;
    }

    public void setSimplePick(PostPickRequestDTO simplePick) {
        this.simplePick = simplePick;
    }

    public PostParleyRequestDTO getParley() {
        return parley;
    }

    public void setParley(PostParleyRequestDTO parley) {
        this.parley = parley;
    }
}
