package com.bilyoner.assignment.balanceapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BalanceApiException extends RuntimeException {

    private final ErrorCodeEnum errorCode;

}
