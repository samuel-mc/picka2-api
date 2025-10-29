package com.samuel_mc.pickados_api.service;

import com.samuel_mc.pickados_api.dto.RegisterRequest;

public interface AuthService {
    void userRegister(RegisterRequest req);
    void tipsterRegister();
}
