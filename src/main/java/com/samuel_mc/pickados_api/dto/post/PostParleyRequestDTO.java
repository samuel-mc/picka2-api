package com.samuel_mc.pickados_api.dto.post;

import com.samuel_mc.pickados_api.entity.enums.ResultStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostParleyRequestDTO {

    @NotNull(message = "El stake es obligatorio")
    @Min(value = 10, message = "El stake mínimo es 10")
    @Max(value = 100, message = "El stake máximo es 100")
    private Integer stake;

    private Long sportsbookId;

    @NotNull(message = "La fecha del parley es obligatoria")
    private LocalDateTime eventDate;

    private ResultStatus resultStatus = ResultStatus.PENDING;

    private List<ParleySelectionRequestDTO> selections = new ArrayList<>();

    public Integer getStake() {
        return stake;
    }

    public void setStake(Integer stake) {
        this.stake = stake;
    }

    public Long getSportsbookId() {
        return sportsbookId;
    }

    public void setSportsbookId(Long sportsbookId) {
        this.sportsbookId = sportsbookId;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public List<ParleySelectionRequestDTO> getSelections() {
        return selections;
    }

    public void setSelections(List<ParleySelectionRequestDTO> selections) {
        this.selections = selections;
    }
}
