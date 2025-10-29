package com.samuel_mc.pickados_api.exception;

import lombok.Getter;

public class GenericException extends RuntimeException {

    @Getter
    private final String code;
    private final String message;

    public GenericException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
