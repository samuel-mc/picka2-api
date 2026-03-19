package com.samuel_mc.pickados_api.util;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.exception.GenericException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ResponseUtils responseUtils;

    public GlobalExceptionHandler(ResponseUtils responseUtils) {
        this.responseUtils = responseUtils;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Datos inválidos");

        GenericException validationEx = new GenericException(
                ResponseCode.VALIDATION_ERROR.getCode(),
                errorMessage
        );

        return responseUtils.generateErrorResponse(validationEx);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<GenericResponseDTO<String>> handleGenericException(GenericException ex) {
        return responseUtils.generateErrorResponse(ex);
    }

    @ExceptionHandler({org.springframework.web.servlet.resource.NoResourceFoundException.class, org.springframework.web.servlet.NoHandlerFoundException.class})
    public ResponseEntity<GenericResponseDTO<String>> handleNotFoundException(Exception ex) {
        GenericException notFoundEx = new GenericException(ResponseCode.NOT_FOUND.getCode(), ResponseCode.NOT_FOUND.getMessage());
        return responseUtils.generateErrorResponse(notFoundEx);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponseDTO<String>> handleUnexpected(Exception ex) {
        GenericException genericEx = new GenericException(ResponseCode.INTERNAL_ERROR.getCode(), ResponseCode.INTERNAL_ERROR.getMessage());
        return responseUtils.generateErrorResponse(genericEx);
    }
}
