package com.samuel_mc.pickados_api.util;

import com.samuel_mc.pickados_api.dto.GenericResponse;
import com.samuel_mc.pickados_api.exception.GenericException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseUtils responseUtils;

    public GlobalExceptionHandler(ResponseUtils responseUtils) {
        this.responseUtils = responseUtils;
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GenericResponse<String>> handleGenericException(GenericException ex) {
        return responseUtils.generateErrorResponse(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<String>> handleUnexpected(Exception ex) {
        GenericException genericEx = new GenericException(ResponseCode.INTERNAL_ERROR.getCode(), ResponseCode.INTERNAL_ERROR.getMessage());
        return responseUtils.generateErrorResponse(genericEx);
    }
}
