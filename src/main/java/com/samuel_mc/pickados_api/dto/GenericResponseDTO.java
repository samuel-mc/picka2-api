package com.samuel_mc.pickados_api.dto;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponseDTO<T> {
    private boolean success;       // Indica si la operación fue exitosa
    private String code;           // Código interno o de error, ej. "OK", "USER_NOT_FOUND", etc.
    private String message;        // Mensaje legible para el usuario o desarrollador
    private T data;                // Cuerpo con los datos reales de la respuesta
    private LocalDateTime timestamp; // Fecha y hora del evento
}