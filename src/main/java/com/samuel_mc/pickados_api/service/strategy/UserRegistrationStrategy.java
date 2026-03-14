package com.samuel_mc.pickados_api.service.strategy;

import com.samuel_mc.pickados_api.dto.RegisterUserRequestDTO;

public interface UserRegistrationStrategy {
    void register(RegisterUserRequestDTO req);

    boolean supports(Class<? extends RegisterUserRequestDTO> requestClass);
}
