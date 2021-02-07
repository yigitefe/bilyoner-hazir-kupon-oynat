package com.bilyoner.assignment.couponapi.exception;

import com.bilyoner.assignment.couponapi.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponApiException.class)
    public ResponseEntity<?> handleApiException(CouponApiException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(exception.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(ErrorCodeEnum.FIELD_VALIDATION_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(ErrorCodeEnum.API_CALL_ERROR, exception.getResponseBodyAsString());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleAnyException(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return buildErrorResponse(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> buildErrorResponse(ErrorCodeEnum errorCode) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build(),
                errorCode.getHttpStatus());
    }

    private ResponseEntity<?> buildErrorResponse(ErrorCodeEnum errorCode, String integrationMessage) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .integrationMessage(integrationMessage)
                        .build(),
                errorCode.getHttpStatus());
    }
}