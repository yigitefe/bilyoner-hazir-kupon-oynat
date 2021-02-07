package com.bilyoner.assignment.balanceapi.exception;

import com.bilyoner.assignment.balanceapi.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BalanceApiException.class)
    public ResponseEntity<?> handleApiException(BalanceApiException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(exception.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        return buildErrorResponse(ErrorCodeEnum.FIELD_VALIDATION_ERROR);
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

}
