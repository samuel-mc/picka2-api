package com.samuel_mc.pickados_api.dto;

public class AuthAvailabilityResponseDTO {
    private Boolean usernameAvailable;
    private Boolean emailAvailable;

    public AuthAvailabilityResponseDTO() {
    }

    public AuthAvailabilityResponseDTO(Boolean usernameAvailable, Boolean emailAvailable) {
        this.usernameAvailable = usernameAvailable;
        this.emailAvailable = emailAvailable;
    }

    public Boolean getUsernameAvailable() {
        return usernameAvailable;
    }

    public void setUsernameAvailable(Boolean usernameAvailable) {
        this.usernameAvailable = usernameAvailable;
    }

    public Boolean getEmailAvailable() {
        return emailAvailable;
    }

    public void setEmailAvailable(Boolean emailAvailable) {
        this.emailAvailable = emailAvailable;
    }
}
