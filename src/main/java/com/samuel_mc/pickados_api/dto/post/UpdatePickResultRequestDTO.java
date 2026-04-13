package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.ResultStatus;
import jakarta.validation.constraints.NotNull;

public class UpdatePickResultRequestDTO {

    @NotNull(message = "El estado del pick es obligatorio")
    private ResultStatus resultStatus;

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }
}
