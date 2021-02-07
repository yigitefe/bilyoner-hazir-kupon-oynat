package com.bilyoner.assignment.balanceapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    INTERNAL_SERVER_ERROR(1000, "Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR),
    FIELD_VALIDATION_ERROR(1001, "Field validation error.", HttpStatus.BAD_REQUEST),
    CONTENT_NOT_FOUND_ERROR(1002, "Content not found.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE(1003, "Insufficient balance.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND_ERROR(1004, "User not found.", HttpStatus.BAD_REQUEST),
    NOT_SUPPORTED_TRANSACTION_TYPE_ERROR(1005, "Not supported transaction type.", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus httpStatus;
}
