package com.samuel_mc.pickados_api.util;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.exception.GenericException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ResponseUtils {
    public <T> ResponseEntity<GenericResponseDTO<T>> generateSuccessResponse(T data) {
        GenericResponseDTO<T> response = GenericResponseDTO.<T>builder()
                .success(true)
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<GenericResponseDTO<T>> generateErrorResponse(GenericException error) {
        GenericResponseDTO<T> response = GenericResponseDTO.<T>builder()
                .success(false)
                .code(error.getCode())
                .message(error.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        HttpStatus status = ResponseCode.getHttpStatusByCode(error.getCode());
        return ResponseEntity.status(status).body(response);
    }

}
