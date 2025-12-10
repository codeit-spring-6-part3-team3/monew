package com.team03.monew.subscribe.exception;

import com.team03.monew.common.customerror.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SubscribeErrorCode implements ErrorCode {

    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND,"구독 정보 없음");

    private final HttpStatus httpStatus;
    private final String message;
    SubscribeErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
