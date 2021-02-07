package com.bilyoner.assignment.couponapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    INTERNAL_SERVER_ERROR(1000, "Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR),
    FIELD_VALIDATION_ERROR(1001, "Field validation error.", HttpStatus.BAD_REQUEST),
    CONTENT_NOT_FOUND_ERROR(1002, "Content not found.", HttpStatus.BAD_REQUEST),
    MBS_CHECK_ERROR(1003, "MBS rules violated.", HttpStatus.BAD_REQUEST),
    INCOMPATIBLE_EVENTS_ERROR(1004, "Incompatible events found.", HttpStatus.BAD_REQUEST),
    EXPIRED_EVENT_ERROR(1005, "Expired event found.", HttpStatus.BAD_REQUEST),
    RECURRING_COUPON_PLAY_ERROR(1006, "Coupons can't be played twice.", HttpStatus.BAD_REQUEST),
    API_CALL_ERROR(1007, "API call error.", HttpStatus.BAD_REQUEST),
    COUPON_NOT_FOUND_ERROR(1008, "Coupon not found.", HttpStatus.BAD_REQUEST),
    CANCEL_PERIOD_EXPIRED_ERROR(1009, "Cancel period expired.", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus httpStatus;
}
