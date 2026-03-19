package com.samuel_mc.pickados_api.util;

import org.springframework.http.HttpStatus;

public enum ResponseCode {

    // Códigos de éxito
    SUCCESS("SUCCESS", "Operación exitosa", HttpStatus.OK),
    CREATED("CREATED", "Recurso creado correctamente", HttpStatus.CREATED),

    // Códigos de error del cliente (4xx)
    BAD_REQUEST("BAD_REQUEST", "Petición inválida", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "No autorizado", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "Acceso denegado", HttpStatus.FORBIDDEN),
    NOT_FOUND("NOT_FOUND", "Recurso no encontrado", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("USER_NOT_FOUND", "El usuario no existe", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("VALIDATION_ERROR", "Error de validación", HttpStatus.BAD_REQUEST),

    // Códigos de error del servidor (5xx)
    INTERNAL_ERROR("INTERNAL_ERROR", "Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "Servicio temporalmente no disponible", HttpStatus.SERVICE_UNAVAILABLE);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ResponseCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ResponseCode fromCode(String code) {
        for (ResponseCode rc : values()) {
            if (rc.code.equalsIgnoreCase(code)) {
                return rc;
            }
        }
        return ResponseCode.INTERNAL_ERROR;
    }

    public static HttpStatus getHttpStatusByCode(String code) {
        for (ResponseCode rc : values()) {
            if (rc.code.equalsIgnoreCase(code)) {
                return rc.status;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // valor por defecto
    }

}
